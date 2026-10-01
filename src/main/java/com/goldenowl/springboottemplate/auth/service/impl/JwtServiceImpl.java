package com.goldenowl.springboottemplate.auth.service.impl;

import com.goldenowl.springboottemplate.auth.entity.PermissionEntity;
import com.goldenowl.springboottemplate.auth.entity.RoleEntity;
import com.goldenowl.springboottemplate.auth.service.JwtService;
import com.goldenowl.springboottemplate.user.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class JwtServiceImpl implements JwtService {

  private static final String REFRESH_TOKEN_TYPE = "refresh";

  private final SecretKey jwtSecretKey;

  @Override
  public String generateToken(UserEntity user, Duration expiry) {
    Instant now = Instant.now();
    return Jwts.builder()
        .id(UUID.randomUUID().toString())
        .subject(user.getUsername())
        .claim("roles", getRoles(user))
        .claim("permissions", getPermissions(user))
        .claim("token_type", "access")
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plus(expiry)))
        .signWith(jwtSecretKey)
        .compact();
  }

  @Override
  public String generateRefreshToken(UserEntity user, Duration expiry) {
    Instant now = Instant.now();
    return Jwts.builder()
        .id(UUID.randomUUID().toString())
        .subject(user.getUsername())
        .claim("token_type", REFRESH_TOKEN_TYPE)
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plus(expiry)))
        .signWith(jwtSecretKey)
        .compact();
  }

  @Override
  public boolean validateToken(String token) {
    try {
      parseClaims(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public boolean isRefreshToken(String token) {
    try {
      return REFRESH_TOKEN_TYPE.equals(parseClaims(token).get("token_type", String.class));
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public String extractUsername(String token) {
    return parseClaims(token).getSubject();
  }

  @Override
  public String extractTokenId(String token) {
    return parseClaims(token).getId();
  }

  @Override
  public Instant extractExpiration(String token) {
    return parseClaims(token).getExpiration().toInstant();
  }

  private Claims parseClaims(String token) {
    return Jwts.parser().verifyWith(jwtSecretKey).build().parseSignedClaims(token).getPayload();
  }

  private List<String> getRoles(UserEntity user) {
    return user.getRoles().stream().map(RoleEntity::getName).toList();
  }

  private List<String> getPermissions(UserEntity user) {
    return user.getRoles().stream()
        .flatMap(role -> role.getPermissions().stream())
        .map(PermissionEntity::getName)
        .toList();
  }
}
