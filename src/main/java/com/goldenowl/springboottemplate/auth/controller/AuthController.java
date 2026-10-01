package com.goldenowl.springboottemplate.auth.controller;

import com.goldenowl.springboottemplate.app.exception.InvalidTokenException;
import com.goldenowl.springboottemplate.auth.dto.LoginRequestDTO;
import com.goldenowl.springboottemplate.auth.dto.LoginResponseDTO;
import com.goldenowl.springboottemplate.auth.dto.RefreshTokenDTO;
import com.goldenowl.springboottemplate.auth.dto.RegistrationDTO;
import com.goldenowl.springboottemplate.auth.dto.TokenResponseDTO;
import com.goldenowl.springboottemplate.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  @ResponseStatus(HttpStatus.OK)
  LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
    return authService.login(loginRequestDTO);
  }

  @PostMapping("/logout")
  @ResponseStatus(HttpStatus.OK)
  void logout(
      @Valid @RequestBody RefreshTokenDTO refreshTokenDTO,
      @RequestHeader(value = "Authorization", required = false) String authHeader) {
    if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
      throw new InvalidTokenException("Authorization Bearer token is required");
    }
    authService.logout(authHeader.substring(7), refreshTokenDTO.refreshToken());
  }

  @PostMapping("/refresh-token")
  @ResponseStatus(HttpStatus.OK)
  TokenResponseDTO refreshToken(@Valid @RequestBody RefreshTokenDTO refreshTokenDTO) {
    return authService.refreshToken(refreshTokenDTO.refreshToken());
  }

  @PostMapping("/sign-up")
  @ResponseStatus(HttpStatus.OK)
  void signUp(@Valid @RequestBody RegistrationDTO registrationDTO) {
    authService.signup(registrationDTO);
  }

  @PostMapping("/verification/{userVerifyToken}")
  @ResponseStatus(HttpStatus.OK)
  void verifyUser(@PathVariable String userVerifyToken) {
    authService.verifyUserRegistration(userVerifyToken);
  }

  @PostMapping("/refresh-user-verification")
  @ResponseStatus(HttpStatus.OK)
  void refreshUserVerification(@RequestParam("username") String username) {
    authService.refreshUserVerification(username);
  }
}
