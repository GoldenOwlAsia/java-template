package com.goldenowl.springboottemplate.auth.service;

import com.goldenowl.springboottemplate.auth.dto.LoginRequestDTO;
import com.goldenowl.springboottemplate.auth.dto.LoginResponseDTO;
import com.goldenowl.springboottemplate.auth.dto.RegistrationDTO;
import com.goldenowl.springboottemplate.auth.dto.TokenResponseDTO;

public interface AuthService {

  LoginResponseDTO login(LoginRequestDTO loginRequestDTO);

  void logout(String accessToken, String refreshToken);

  TokenResponseDTO refreshToken(String refreshToken);

  LoginResponseDTO getLoginResponseWithAssignedTokens(String username);

  void signup(RegistrationDTO registrationDTO);

  void verifyUserRegistration(String token);

  void refreshUserVerification(String username);
}
