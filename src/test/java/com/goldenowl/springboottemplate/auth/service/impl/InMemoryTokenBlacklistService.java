package com.goldenowl.springboottemplate.auth.service.impl;

import com.goldenowl.springboottemplate.app.constant.ProfileConstant;
import com.goldenowl.springboottemplate.auth.service.TokenBlacklistService;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile(ProfileConstant.TEST)
class InMemoryTokenBlacklistService implements TokenBlacklistService {

  private final Set<String> accessTokens = ConcurrentHashMap.newKeySet();

  private final Set<String> refreshTokens = ConcurrentHashMap.newKeySet();

  @Override
  public void blacklistAccessToken(String token) {
    if (token != null && !token.isBlank()) {
      accessTokens.add(token);
    }
  }

  @Override
  public void blacklistRefreshToken(String refreshToken) {
    if (refreshToken != null && !refreshToken.isBlank()) {
      refreshTokens.add(refreshToken);
    }
  }

  @Override
  public boolean tryConsumeRefreshToken(String refreshToken) {
    if (refreshToken == null || refreshToken.isBlank()) {
      return false;
    }
    return refreshTokens.add(refreshToken);
  }

  @Override
  public boolean isAccessTokenBlacklisted(String token) {
    return token != null && accessTokens.contains(token);
  }
}
