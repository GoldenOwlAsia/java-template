package com.goldenowl.springboottemplate.auth.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.goldenowl.springboottemplate.auth.service.JwtService;
import io.lettuce.core.RedisConnectionException;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class TokenBlacklistServiceImplTest {

  private static final String TOKEN = "jwt-token";

  private static final String JTI = "token-jti-1";

  @Mock private StringRedisTemplate stringRedisTemplate;

  @Mock private JwtService jwtService;

  @Mock private ValueOperations<String, String> valueOperations;

  @InjectMocks private TokenBlacklistServiceImpl blacklistService;

  @Test
  void blacklistAccessToken_storesJtiKeyWithJwtTtl() {
    when(jwtService.extractTokenId(TOKEN)).thenReturn(JTI);
    when(jwtService.extractExpiration(TOKEN)).thenReturn(Instant.now().plusSeconds(120));
    when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);

    blacklistService.blacklistAccessToken(TOKEN);

    ArgumentCaptor<Duration> ttlCaptor = ArgumentCaptor.forClass(Duration.class);
    verify(valueOperations).set(eq("auth:blacklist:access:" + JTI), eq("1"), ttlCaptor.capture());
    assertTrue(ttlCaptor.getValue().toSeconds() > 0);
    assertTrue(ttlCaptor.getValue().compareTo(Duration.ofSeconds(120)) <= 0);
  }

  @Test
  void blacklistRefreshToken_storesJtiKeyWithRefreshPrefix() {
    when(jwtService.extractTokenId(TOKEN)).thenReturn(JTI);
    when(jwtService.extractExpiration(TOKEN)).thenReturn(Instant.now().plusSeconds(60));
    when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);

    blacklistService.blacklistRefreshToken(TOKEN);

    verify(valueOperations).set(eq("auth:blacklist:refresh:" + JTI), eq("1"), any(Duration.class));
  }

  @Test
  void tryConsumeRefreshToken_returnsTrueOnFirstUse() {
    when(jwtService.extractTokenId(TOKEN)).thenReturn(JTI);
    when(jwtService.extractExpiration(TOKEN)).thenReturn(Instant.now().plusSeconds(60));
    when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.setIfAbsent(
            eq("auth:blacklist:refresh:" + JTI), eq("1"), any(Duration.class)))
        .thenReturn(true);

    assertTrue(blacklistService.tryConsumeRefreshToken(TOKEN));
  }

  @Test
  void tryConsumeRefreshToken_returnsFalseWhenAlreadyUsed() {
    when(jwtService.extractTokenId(TOKEN)).thenReturn(JTI);
    when(jwtService.extractExpiration(TOKEN)).thenReturn(Instant.now().plusSeconds(60));
    when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.setIfAbsent(
            eq("auth:blacklist:refresh:" + JTI), eq("1"), any(Duration.class)))
        .thenReturn(false);

    assertFalse(blacklistService.tryConsumeRefreshToken(TOKEN));
  }

  @Test
  void tryConsumeRefreshToken_allowsWhenRedisDown() {
    when(jwtService.extractTokenId(TOKEN)).thenReturn(JTI);
    when(jwtService.extractExpiration(TOKEN)).thenReturn(Instant.now().plusSeconds(60));
    when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class)))
        .thenThrow(new RedisConnectionException("down"));

    assertTrue(blacklistService.tryConsumeRefreshToken(TOKEN));
  }

  @Test
  void blacklistAccessToken_skipsExpiredToken() {
    when(jwtService.extractTokenId(TOKEN)).thenReturn(JTI);
    when(jwtService.extractExpiration(TOKEN)).thenReturn(Instant.now().minusSeconds(5));

    blacklistService.blacklistAccessToken(TOKEN);

    verify(stringRedisTemplate, never()).opsForValue();
  }

  @Test
  void blacklistAccessToken_skipsBlankToken() {
    blacklistService.blacklistAccessToken(" ");

    verifyNoInteractions(jwtService, stringRedisTemplate);
  }

  @Test
  void blacklistAccessToken_skipsInvalidJwt() {
    when(jwtService.extractTokenId(TOKEN)).thenThrow(new IllegalArgumentException("bad jwt"));

    blacklistService.blacklistAccessToken(TOKEN);

    verify(stringRedisTemplate, never()).opsForValue();
  }

  @Test
  void blacklistAccessToken_skipsWhenRedisDown() {
    when(jwtService.extractTokenId(TOKEN)).thenReturn(JTI);
    when(jwtService.extractExpiration(TOKEN)).thenReturn(Instant.now().plusSeconds(60));
    when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
    doThrow(new RedisConnectionException("down"))
        .when(valueOperations)
        .set(anyString(), anyString(), any(Duration.class));

    assertDoesNotThrow(() -> blacklistService.blacklistAccessToken(TOKEN));
  }

  @Test
  void isAccessTokenBlacklisted_returnsFalseWhenRedisDown() {
    when(jwtService.extractTokenId(TOKEN)).thenReturn(JTI);
    when(stringRedisTemplate.hasKey("auth:blacklist:access:" + JTI))
        .thenThrow(new RedisConnectionException("down"));

    assertFalse(blacklistService.isAccessTokenBlacklisted(TOKEN));
  }

  @Test
  void isAccessTokenBlacklisted_returnsTrueWhenKeyExists() {
    when(jwtService.extractTokenId(TOKEN)).thenReturn(JTI);
    when(stringRedisTemplate.hasKey("auth:blacklist:access:" + JTI)).thenReturn(true);

    assertTrue(blacklistService.isAccessTokenBlacklisted(TOKEN));
  }

  @Test
  void isAccessTokenBlacklisted_returnsFalseWhenKeyMissing() {
    when(jwtService.extractTokenId(TOKEN)).thenReturn(JTI);
    when(stringRedisTemplate.hasKey("auth:blacklist:access:" + JTI)).thenReturn(false);

    assertFalse(blacklistService.isAccessTokenBlacklisted(TOKEN));
  }

  @Test
  void isAccessTokenBlacklisted_blankTokenIsNotBlacklisted() {
    assertFalse(blacklistService.isAccessTokenBlacklisted(" "));
    verifyNoInteractions(stringRedisTemplate);
  }
}
