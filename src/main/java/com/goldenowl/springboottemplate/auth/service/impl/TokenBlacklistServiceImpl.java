package com.goldenowl.springboottemplate.auth.service.impl;

import com.goldenowl.springboottemplate.app.constant.ProfileConstant;
import com.goldenowl.springboottemplate.auth.service.JwtService;
import com.goldenowl.springboottemplate.auth.service.TokenBlacklistService;
import java.time.Duration;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Profile("!" + ProfileConstant.TEST)
@RequiredArgsConstructor
@Slf4j
class TokenBlacklistServiceImpl implements TokenBlacklistService {

  private static final String ACCESS_TOKEN_KEY_PREFIX = "auth:blacklist:access:";

  private static final String REFRESH_TOKEN_KEY_PREFIX = "auth:blacklist:refresh:";

  private final StringRedisTemplate stringRedisTemplate;

  private final JwtService jwtService;

  @Override
  public void blacklistAccessToken(String token) {
    blacklist(ACCESS_TOKEN_KEY_PREFIX, token);
  }

  @Override
  public void blacklistRefreshToken(String refreshToken) {
    blacklist(REFRESH_TOKEN_KEY_PREFIX, refreshToken);
  }

  @Override
  public boolean tryConsumeRefreshToken(String refreshToken) {
    BlacklistEntry entry = resolveEntry(REFRESH_TOKEN_KEY_PREFIX, refreshToken);
    if (entry == null) {
      return false;
    }
    try {
      Boolean firstUse =
          stringRedisTemplate.opsForValue().setIfAbsent(entry.key(), "1", entry.ttl());
      return Boolean.TRUE.equals(firstUse);
    } catch (Exception ex) {
      log.warn("Redis unavailable; allow refresh token consume: {}", ex.getMessage());
      return true;
    }
  }

  @Override
  public boolean isAccessTokenBlacklisted(String token) {
    if (!StringUtils.hasText(token)) {
      return false;
    }
    try {
      String jti = jwtService.extractTokenId(token);
      if (!StringUtils.hasText(jti)) {
        return false;
      }
      return Boolean.TRUE.equals(stringRedisTemplate.hasKey(ACCESS_TOKEN_KEY_PREFIX + jti));
    } catch (Exception ex) {
      log.warn("Redis unavailable; treat token as not blacklisted: {}", ex.getMessage());
      return false;
    }
  }

  private void blacklist(String keyPrefix, String token) {
    BlacklistEntry entry = resolveEntry(keyPrefix, token);
    if (entry == null) {
      return;
    }
    try {
      stringRedisTemplate.opsForValue().set(entry.key(), "1", entry.ttl());
    } catch (Exception ex) {
      log.warn("Redis unavailable; skip blacklisting token: {}", ex.getMessage());
    }
  }

  private BlacklistEntry resolveEntry(String keyPrefix, String token) {
    if (!StringUtils.hasText(token)) {
      return null;
    }
    try {
      String jti = jwtService.extractTokenId(token);
      Instant expiration = jwtService.extractExpiration(token);
      if (!StringUtils.hasText(jti)) {
        return null;
      }
      Duration ttl = Duration.between(Instant.now(), expiration);
      if (ttl.isNegative() || ttl.isZero()) {
        return null;
      }
      return new BlacklistEntry(keyPrefix + jti, ttl);
    } catch (Exception ex) {
      log.warn("Skip blacklisting invalid or unreadable token: {}", ex.getMessage());
      return null;
    }
  }

  private record BlacklistEntry(String key, Duration ttl) {}
}
