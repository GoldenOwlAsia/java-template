package com.goldenowl.springboottemplate.auth.service;

public interface TokenBlacklistService {

  void blacklistAccessToken(String token);

  void blacklistRefreshToken(String refreshToken);

  boolean tryConsumeRefreshToken(String refreshToken);

  boolean isAccessTokenBlacklisted(String token);
}
