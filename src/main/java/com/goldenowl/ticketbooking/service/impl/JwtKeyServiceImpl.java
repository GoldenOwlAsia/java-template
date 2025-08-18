package com.goldenowl.ticketbooking.service.impl;

import com.goldenowl.ticketbooking.entity.PermissionEntity;
import com.goldenowl.ticketbooking.entity.RoleEntity;
import com.goldenowl.ticketbooking.properties.JwtProperties;
import com.goldenowl.ticketbooking.service.JwtKeyService;
import com.goldenowl.ticketbooking.entity.UserEntity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
@Transactional
class JwtKeyServiceImpl implements JwtKeyService {

    private final Key secretKey;

    public JwtKeyServiceImpl(JwtProperties jwtProperties) {
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties
                .getSecretKey().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generateToken(UserEntity user, Duration expiry) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("username", user.getUsername())
                .claim("roles", getRoles(user))
                .claim("permissions", getPermissions(user))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(expiry)))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String generateRefreshToken(UserEntity user, Duration expiry) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("token_type", "refresh")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(expiry)))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    private List<String> getRoles(UserEntity user) {
        return user.getRoles()
                .stream()
                .map(RoleEntity::getName)
                .toList();
    }

    private List<String> getPermissions(UserEntity user) {
        return user.getRoles()
                .stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(PermissionEntity::getName)
                .toList();
    }
}
