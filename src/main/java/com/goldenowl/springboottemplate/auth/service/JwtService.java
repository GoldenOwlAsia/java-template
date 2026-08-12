package com.goldenowl.springboottemplate.auth.service;

import com.goldenowl.springboottemplate.user.entity.UserEntity;
import java.time.Duration;
import java.time.Instant;

public interface JwtService {

  String generateToken(UserEntity user, Duration expiry);

  String generateRefreshToken(UserEntity user, Duration expiry);

  boolean validateToken(String token);

  boolean isRefreshToken(String token);

  String extractUsername(String token);

  String extractTokenId(String token);

  Instant extractExpiration(String token);
}
