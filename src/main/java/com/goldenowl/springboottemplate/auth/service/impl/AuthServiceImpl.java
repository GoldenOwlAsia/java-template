package com.goldenowl.springboottemplate.auth.service.impl;

import com.goldenowl.springboottemplate.app.exception.BlacklistedTokenException;
import com.goldenowl.springboottemplate.app.exception.InvalidTokenException;
import com.goldenowl.springboottemplate.app.exception.LoginNotValidException;
import com.goldenowl.springboottemplate.app.exception.ResourceAlreadyExistsException;
import com.goldenowl.springboottemplate.app.exception.ResourceNotFoundException;
import com.goldenowl.springboottemplate.app.exception.SignUpNotValidException;
import com.goldenowl.springboottemplate.app.utils.TimeUtils;
import com.goldenowl.springboottemplate.auth.dto.LoginRequestDTO;
import com.goldenowl.springboottemplate.auth.dto.LoginResponseDTO;
import com.goldenowl.springboottemplate.auth.dto.RegistrationDTO;
import com.goldenowl.springboottemplate.auth.dto.TokenResponseDTO;
import com.goldenowl.springboottemplate.auth.entity.RoleEntity;
import com.goldenowl.springboottemplate.auth.enumeration.UserDefaultType;
import com.goldenowl.springboottemplate.auth.mapper.AuthMapper;
import com.goldenowl.springboottemplate.auth.properties.JwtProperties;
import com.goldenowl.springboottemplate.auth.service.AuthService;
import com.goldenowl.springboottemplate.auth.service.JwtService;
import com.goldenowl.springboottemplate.auth.service.RoleService;
import com.goldenowl.springboottemplate.auth.service.TokenBlacklistService;
import com.goldenowl.springboottemplate.email.dto.CompleteUserMailDTO;
import com.goldenowl.springboottemplate.email.service.MailService;
import com.goldenowl.springboottemplate.user.entity.UserEntity;
import com.goldenowl.springboottemplate.user.enumeration.UserStatus;
import com.goldenowl.springboottemplate.user.repository.UserRepository;
import com.goldenowl.springboottemplate.user.service.UserService;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
class AuthServiceImpl implements AuthService {

  private static final int EXPIRED_VERIFICATION_TOKEN_SECONDS = 300;

  private final AuthMapper authMapper;

  private final UserRepository userRepository;

  private final UserService userService;

  private final RoleService roleService;

  private final PasswordEncoder passwordEncoder;

  private final JwtProperties jwtProperties;

  private final JwtService jwtService;

  private final TokenBlacklistService tokenBlacklistService;

  private final MailService mailService;

  @Override
  public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
    log.info("Login attempt for username: {}", loginRequestDTO.getUsername());

    UserEntity user;
    try {
      user = userService.getUserByUsername(loginRequestDTO.getUsername());
    } catch (ResourceNotFoundException ex) {
      throw new LoginNotValidException("Invalid username or password");
    }

    if (user.getStatus() != UserStatus.ACTIVE
        || user.getPassword() == null
        || !passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
      log.warn("Invalid login attempt for user: {}", loginRequestDTO.getUsername());
      throw new LoginNotValidException("Invalid username or password");
    }

    log.info("User {} logged in successfully", loginRequestDTO.getUsername());
    return getLoginResponseWithAssignedTokens(user.getUsername());
  }

  @Override
  public void logout(String accessToken, String refreshToken) {
    tokenBlacklistService.blacklistAccessToken(accessToken);
    tokenBlacklistService.blacklistRefreshToken(refreshToken);
  }

  @Override
  public TokenResponseDTO refreshToken(String refreshToken) {
    if (!jwtService.validateToken(refreshToken) || !jwtService.isRefreshToken(refreshToken)) {
      throw new InvalidTokenException("Invalid or expired refresh token");
    }
    if (!tokenBlacklistService.tryConsumeRefreshToken(refreshToken)) {
      throw new BlacklistedTokenException("Refresh token is blacklisted or already used");
    }

    String username = jwtService.extractUsername(refreshToken);
    UserEntity user = userService.getUserByUsername(username);

    String newAccessToken = jwtService.generateToken(user, jwtProperties.getTokenExp());
    String newRefreshToken =
        jwtService.generateRefreshToken(user, jwtProperties.getRefreshTokenExp());

    return TokenResponseDTO.builder()
        .accessToken(newAccessToken)
        .refreshToken(newRefreshToken)
        .build();
  }

  @Override
  public LoginResponseDTO getLoginResponseWithAssignedTokens(String username) {
    UserEntity user = userService.getUserByUsername(username);
    LoginResponseDTO responseDTO = authMapper.mapToLoginResponseDTO(user);

    responseDTO.setToken(jwtService.generateToken(user, jwtProperties.getTokenExp()));
    responseDTO.setRefreshToken(
        jwtService.generateRefreshToken(user, jwtProperties.getRefreshTokenExp()));

    log.info("Generated tokens for user: {}", user.getUsername());
    return responseDTO;
  }

  @Override
  public void signup(RegistrationDTO registrationDTO) {
    log.info("Signing up user: {}", registrationDTO.getUsername());
    checkExistingUser(registrationDTO);

    UserEntity user = authMapper.mapToEntity(registrationDTO, new UserEntity());
    user.setStatus(UserStatus.PENDING);
    user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
    assignNewVerifyToken(user);

    userRepository.save(user);
    log.info("User {} signed up successfully with status PENDING", registrationDTO.getUsername());
  }

  @Override
  public void verifyUserRegistration(String token) {
    log.info("Verifying user registration");
    UserEntity user =
        userRepository
            .findByCurrentVerificationTokenAndStatus(token, UserStatus.PENDING)
            .orElseThrow(() -> new SignUpNotValidException("Token is not valid"));

    if (user.getExpiredVerificationTokenDate() == null) {
      throw new SignUpNotValidException("Token is not valid");
    }
    if (LocalDateTime.now().isAfter(user.getExpiredVerificationTokenDate())) {
      throw new SignUpNotValidException("Token is expired");
    }

    RoleEntity userRole = roleService.getRoleByName(UserDefaultType.USER.name());
    user.setStatus(UserStatus.ACTIVE);
    // Mutable set required — Hibernate replaces collection elements via clear().
    Set<RoleEntity> roles = user.getRoles();
    if (roles == null) {
      roles = new HashSet<>();
      user.setRoles(roles);
    } else {
      roles.clear();
    }
    roles.add(userRole);
    user.setCurrentVerificationToken(null);
    user.setExpiredVerificationTokenDate(null);
    userRepository.save(user);

    // Welcome mail must not roll back activation (e.g. missing SMTP credentials in
    // local/demo).
    try {
      mailService.sendCompleteUserMail(
          CompleteUserMailDTO.builder()
              .email(user.getEmail())
              .name(user.getName())
              .username(user.getUsername())
              .createdAt(user.getCreatedAt())
              .build());
    } catch (Exception ex) {
      log.warn(
          "User '{}' activated but welcome email failed: {}", user.getUsername(), ex.getMessage());
    }

    log.info("User '{}' verified and activated successfully", user.getUsername());
  }

  @Override
  public void refreshUserVerification(String username) {
    log.info("Refreshing verification token for username: {}", username);
    userRepository
        .findByUsernameAndStatusIn(username, List.of(UserStatus.PENDING))
        .ifPresent(
            user -> {
              if (user.getExpiredVerificationTokenDate() != null
                  && LocalDateTime.now().isBefore(user.getExpiredVerificationTokenDate())) {
                log.debug("Verification token still valid for username: {}", username);
                return;
              }
              assignNewVerifyToken(user);
              userRepository.save(user);
            });
  }

  private void checkExistingUser(RegistrationDTO dto) {
    if (userRepository.existsByUsername(dto.getUsername())) {
      throw new ResourceAlreadyExistsException("User", "username", dto.getUsername());
    }
    if (userRepository.existsByEmail(dto.getEmail())) {
      throw new ResourceAlreadyExistsException("User", "email", dto.getEmail());
    }
  }

  private void assignNewVerifyToken(UserEntity user) {
    user.setCurrentVerificationToken(UUID.randomUUID().toString());
    user.setExpiredVerificationTokenDate(
        TimeUtils.getExpiredTime(EXPIRED_VERIFICATION_TOKEN_SECONDS));
    log.debug("Assigned new verification token for user {}", user.getUsername());
  }
}
