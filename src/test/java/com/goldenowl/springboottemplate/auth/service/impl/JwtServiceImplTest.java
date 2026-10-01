package com.goldenowl.springboottemplate.auth.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.goldenowl.springboottemplate.auth.entity.PermissionEntity;
import com.goldenowl.springboottemplate.auth.entity.RoleEntity;
import com.goldenowl.springboottemplate.user.entity.UserEntity;
import com.goldenowl.springboottemplate.user.enumeration.UserStatus;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Set;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtServiceImplTest {

  private SecretKey secretKey;

  private JwtServiceImpl jwtService;

  private UserEntity user;

  @BeforeEach
  void setUp() {
    secretKey =
        Keys.hmacShaKeyFor("my-super-secure-jwt-secret-key-test".getBytes(StandardCharsets.UTF_8));
    jwtService = new JwtServiceImpl(secretKey);

    PermissionEntity permission = new PermissionEntity();
    permission.setName("ARTICLE_READ");
    RoleEntity role = new RoleEntity();
    role.setName("USER");
    role.setPermissions(Set.of(permission));

    user =
        UserEntity.builder()
            .username("alice")
            .email("alice@example.com")
            .name("Alice")
            .status(UserStatus.ACTIVE)
            .roles(Set.of(role))
            .build();
  }

  @Test
  void generateToken_isAccessTokenWithSubject() {
    String token = jwtService.generateToken(user, Duration.ofMinutes(5));

    assertTrue(jwtService.validateToken(token));
    assertFalse(jwtService.isRefreshToken(token));
    assertEquals("alice", jwtService.extractUsername(token));
    assertTrue(jwtService.extractExpiration(token).isAfter(Instant.now()));
  }

  @Test
  void generateRefreshToken_hasRefreshTypeAndJti() {
    String token = jwtService.generateRefreshToken(user, Duration.ofDays(1));

    assertTrue(jwtService.validateToken(token));
    assertTrue(jwtService.isRefreshToken(token));
    assertEquals("alice", jwtService.extractUsername(token));
    assertFalse(jwtService.extractTokenId(token).isBlank());
  }

  @Test
  void generateToken_includesJti() {
    String token = jwtService.generateToken(user, Duration.ofMinutes(5));

    assertFalse(jwtService.extractTokenId(token).isBlank());
  }

  @Test
  void validateToken_rejectsGarbage() {
    assertFalse(jwtService.validateToken("not-a-jwt"));
    assertFalse(jwtService.isRefreshToken("not-a-jwt"));
  }

  @Test
  void validateToken_rejectsExpiredToken() {
    assertFalse(jwtService.validateToken(expiredAccessToken()));
  }

  @Test
  void extractExpiration_returnsClaim() {
    Instant before = Instant.now();
    String token = jwtService.generateToken(user, Duration.ofMinutes(10));
    Instant expiration = jwtService.extractExpiration(token);

    assertTrue(expiration.isAfter(before.plusSeconds(9 * 60)));
    assertTrue(expiration.isBefore(before.plusSeconds(11 * 60)));
  }

  @Test
  void extractUsername_rejectsBadSignature() {
    String token = jwtService.generateToken(user, Duration.ofMinutes(5));
    SecretKey otherKey =
        Keys.hmacShaKeyFor("another-super-secure-jwt-secret-key!".getBytes(StandardCharsets.UTF_8));
    String resigned =
        Jwts.builder()
            .subject("alice")
            .claim("token_type", "access")
            .issuedAt(new Date())
            .expiration(Date.from(Instant.now().plusSeconds(300)))
            .signWith(otherKey)
            .compact();

    assertThrows(SignatureException.class, () -> jwtService.extractUsername(resigned));
    assertTrue(jwtService.validateToken(token));
  }

  @Test
  void extractExpiration_rejectsExpiredToken() {
    assertThrows(
        ExpiredJwtException.class, () -> jwtService.extractExpiration(expiredAccessToken()));
  }

  private String expiredAccessToken() {
    Instant expiredAt = Instant.now().minusSeconds(60);
    return Jwts.builder()
        .subject(user.getUsername())
        .claim("token_type", "access")
        .issuedAt(Date.from(expiredAt.minusSeconds(120)))
        .expiration(Date.from(expiredAt))
        .signWith(secretKey)
        .compact();
  }
}
