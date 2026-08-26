package com.goldenowl.springboottemplate.auth.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.goldenowl.springboottemplate.app.exception.BlacklistedTokenException;
import com.goldenowl.springboottemplate.app.exception.InvalidTokenException;
import com.goldenowl.springboottemplate.app.exception.LoginNotValidException;
import com.goldenowl.springboottemplate.app.exception.ResourceAlreadyExistsException;
import com.goldenowl.springboottemplate.app.exception.ResourceNotFoundException;
import com.goldenowl.springboottemplate.app.exception.SignUpNotValidException;
import com.goldenowl.springboottemplate.auth.dto.LoginRequestDTO;
import com.goldenowl.springboottemplate.auth.dto.LoginResponseDTO;
import com.goldenowl.springboottemplate.auth.dto.RegistrationDTO;
import com.goldenowl.springboottemplate.auth.dto.TokenResponseDTO;
import com.goldenowl.springboottemplate.auth.entity.RoleEntity;
import com.goldenowl.springboottemplate.auth.mapper.AuthMapper;
import com.goldenowl.springboottemplate.auth.properties.JwtProperties;
import com.goldenowl.springboottemplate.auth.service.JwtService;
import com.goldenowl.springboottemplate.auth.service.RoleService;
import com.goldenowl.springboottemplate.auth.service.TokenBlacklistService;
import com.goldenowl.springboottemplate.email.service.MailService;
import com.goldenowl.springboottemplate.user.entity.UserEntity;
import com.goldenowl.springboottemplate.user.enumeration.UserStatus;
import com.goldenowl.springboottemplate.user.repository.UserRepository;
import com.goldenowl.springboottemplate.user.service.UserService;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

  @Mock private AuthMapper authMapper;

  @Mock private UserRepository userRepository;

  @Mock private UserService userService;

  @Mock private RoleService roleService;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private JwtProperties jwtProperties;

  @Mock private JwtService jwtService;

  @Mock private TokenBlacklistService tokenBlacklistService;

  @Mock private MailService mailService;

  @InjectMocks private AuthServiceImpl authService;

  private UserEntity activeUser;

  @BeforeEach
  void setUp() {
    activeUser =
        UserEntity.builder()
            .username("admin")
            .password("encoded")
            .email("admin@example.com")
            .name("Admin")
            .status(UserStatus.ACTIVE)
            .roles(new HashSet<>())
            .build();
  }

  @Test
  void login_returnsTokensForActiveUser() {
    LoginResponseDTO mapped = new LoginResponseDTO();
    mapped.setUsername("admin");

    when(userService.getUserByUsername("admin")).thenReturn(activeUser);
    when(passwordEncoder.matches("secret", "encoded")).thenReturn(true);
    when(authMapper.mapToLoginResponseDTO(activeUser)).thenReturn(mapped);
    when(jwtProperties.getTokenExp()).thenReturn(Duration.ofMinutes(15));
    when(jwtProperties.getRefreshTokenExp()).thenReturn(Duration.ofDays(7));
    when(jwtService.generateToken(activeUser, Duration.ofMinutes(15))).thenReturn("access");
    when(jwtService.generateRefreshToken(activeUser, Duration.ofDays(7))).thenReturn("refresh");

    LoginResponseDTO response = authService.login(loginRequest("admin", "secret"));

    assertEquals("admin", response.getUsername());
    assertEquals("access", response.getToken());
    assertEquals("refresh", response.getRefreshToken());
  }

  @Test
  void login_rejectsUnknownUsername() {
    when(userService.getUserByUsername("missing"))
        .thenThrow(new ResourceNotFoundException("User not found"));

    assertThrows(
        LoginNotValidException.class, () -> authService.login(loginRequest("missing", "secret")));
    verifyNoInteractions(passwordEncoder, jwtService);
  }

  @Test
  void login_rejectsInactiveUser() {
    activeUser.setStatus(UserStatus.PENDING);
    when(userService.getUserByUsername("admin")).thenReturn(activeUser);

    assertThrows(
        LoginNotValidException.class, () -> authService.login(loginRequest("admin", "secret")));
  }

  @Test
  void login_rejectsNullPassword() {
    activeUser.setPassword(null);
    when(userService.getUserByUsername("admin")).thenReturn(activeUser);

    assertThrows(
        LoginNotValidException.class, () -> authService.login(loginRequest("admin", "secret")));
  }

  @Test
  void login_rejectsWrongPassword() {
    when(userService.getUserByUsername("admin")).thenReturn(activeUser);
    when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

    assertThrows(
        LoginNotValidException.class, () -> authService.login(loginRequest("admin", "wrong")));
  }

  @Test
  void logout_blacklistsAccessAndRefreshTokens() {
    authService.logout("access", "refresh");

    verify(tokenBlacklistService).blacklistAccessToken("access");
    verify(tokenBlacklistService).blacklistRefreshToken("refresh");
  }

  @Test
  void refreshToken_rejectsInvalidToken() {
    when(jwtService.validateToken("bad")).thenReturn(false);

    assertThrows(InvalidTokenException.class, () -> authService.refreshToken("bad"));
    verify(tokenBlacklistService, never()).tryConsumeRefreshToken(any());
  }

  @Test
  void refreshToken_rejectsNonRefreshToken() {
    when(jwtService.validateToken("access")).thenReturn(true);
    when(jwtService.isRefreshToken("access")).thenReturn(false);

    assertThrows(InvalidTokenException.class, () -> authService.refreshToken("access"));
  }

  @Test
  void refreshToken_rejectsAlreadyUsedOrBlacklistedToken() {
    when(jwtService.validateToken("refresh")).thenReturn(true);
    when(jwtService.isRefreshToken("refresh")).thenReturn(true);
    when(tokenBlacklistService.tryConsumeRefreshToken("refresh")).thenReturn(false);

    assertThrows(BlacklistedTokenException.class, () -> authService.refreshToken("refresh"));
  }

  @Test
  void refreshToken_rotatesTokensAndConsumesOldRefresh() {
    when(jwtService.validateToken("refresh")).thenReturn(true);
    when(jwtService.isRefreshToken("refresh")).thenReturn(true);
    when(tokenBlacklistService.tryConsumeRefreshToken("refresh")).thenReturn(true);
    when(jwtService.extractUsername("refresh")).thenReturn("admin");
    when(userService.getUserByUsername("admin")).thenReturn(activeUser);
    when(jwtProperties.getTokenExp()).thenReturn(Duration.ofMinutes(15));
    when(jwtProperties.getRefreshTokenExp()).thenReturn(Duration.ofDays(7));
    when(jwtService.generateToken(activeUser, Duration.ofMinutes(15))).thenReturn("new-access");
    when(jwtService.generateRefreshToken(activeUser, Duration.ofDays(7))).thenReturn("new-refresh");

    TokenResponseDTO response = authService.refreshToken("refresh");

    assertEquals("new-access", response.getAccessToken());
    assertEquals("new-refresh", response.getRefreshToken());
    verify(tokenBlacklistService).tryConsumeRefreshToken("refresh");
    verify(tokenBlacklistService, never()).blacklistRefreshToken(any());
  }

  @Test
  void getLoginResponseWithAssignedTokens_mapsUserAndSetsTokens() {
    LoginResponseDTO mapped = new LoginResponseDTO();
    mapped.setUsername("admin");
    when(userService.getUserByUsername("admin")).thenReturn(activeUser);
    when(authMapper.mapToLoginResponseDTO(activeUser)).thenReturn(mapped);
    when(jwtProperties.getTokenExp()).thenReturn(Duration.ofMinutes(15));
    when(jwtProperties.getRefreshTokenExp()).thenReturn(Duration.ofDays(7));
    when(jwtService.generateToken(activeUser, Duration.ofMinutes(15))).thenReturn("access");
    when(jwtService.generateRefreshToken(activeUser, Duration.ofDays(7))).thenReturn("refresh");

    LoginResponseDTO response = authService.getLoginResponseWithAssignedTokens("admin");

    assertEquals("access", response.getToken());
    assertEquals("refresh", response.getRefreshToken());
  }

  @Test
  void signup_rejectsExistingUsername() {
    RegistrationDTO dto = registration("bob", "bob@example.com");
    when(userRepository.existsByUsername("bob")).thenReturn(true);

    assertThrows(ResourceAlreadyExistsException.class, () -> authService.signup(dto));
    verify(userRepository, never()).save(any());
  }

  @Test
  void signup_rejectsExistingEmail() {
    RegistrationDTO dto = registration("bob", "bob@example.com");
    when(userRepository.existsByUsername("bob")).thenReturn(false);
    when(userRepository.existsByEmail("bob@example.com")).thenReturn(true);

    assertThrows(ResourceAlreadyExistsException.class, () -> authService.signup(dto));
    verify(userRepository, never()).save(any());
  }

  @Test
  void signup_persistsPendingUserWithEncodedPasswordAndVerifyToken() {
    RegistrationDTO dto = registration("bob", "bob@example.com");
    UserEntity mapped = new UserEntity();
    when(userRepository.existsByUsername("bob")).thenReturn(false);
    when(userRepository.existsByEmail("bob@example.com")).thenReturn(false);
    when(authMapper.mapToEntity(eq(dto), any(UserEntity.class))).thenReturn(mapped);
    when(passwordEncoder.encode("Password1")).thenReturn("encoded-pass");

    authService.signup(dto);

    ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
    verify(userRepository).save(captor.capture());
    UserEntity saved = captor.getValue();
    assertEquals(UserStatus.PENDING, saved.getStatus());
    assertEquals("encoded-pass", saved.getPassword());
    assertNotNull(saved.getCurrentVerificationToken());
    assertNotNull(saved.getExpiredVerificationTokenDate());
  }

  @Test
  void verifyUserRegistration_rejectsUnknownToken() {
    when(userRepository.findByCurrentVerificationTokenAndStatus("token", UserStatus.PENDING))
        .thenReturn(Optional.empty());

    assertThrows(SignUpNotValidException.class, () -> authService.verifyUserRegistration("token"));
  }

  @Test
  void verifyUserRegistration_rejectsNullExpiry() {
    UserEntity pending = pendingUser(null);
    when(userRepository.findByCurrentVerificationTokenAndStatus("token", UserStatus.PENDING))
        .thenReturn(Optional.of(pending));

    assertThrows(SignUpNotValidException.class, () -> authService.verifyUserRegistration("token"));
  }

  @Test
  void verifyUserRegistration_rejectsExpiredToken() {
    UserEntity pending = pendingUser(LocalDateTime.now().minusMinutes(1));
    when(userRepository.findByCurrentVerificationTokenAndStatus("token", UserStatus.PENDING))
        .thenReturn(Optional.of(pending));

    assertThrows(SignUpNotValidException.class, () -> authService.verifyUserRegistration("token"));
  }

  @Test
  void verifyUserRegistration_activatesUserAndSendsWelcomeMail() {
    UserEntity pending = pendingUser(LocalDateTime.now().plusMinutes(10));
    pending.setRoles(new HashSet<>());
    RoleEntity userRole = new RoleEntity();
    userRole.setName("USER");

    when(userRepository.findByCurrentVerificationTokenAndStatus("token", UserStatus.PENDING))
        .thenReturn(Optional.of(pending));
    when(roleService.getRoleByName("USER")).thenReturn(userRole);

    authService.verifyUserRegistration("token");

    assertEquals(UserStatus.ACTIVE, pending.getStatus());
    assertNull(pending.getCurrentVerificationToken());
    assertNull(pending.getExpiredVerificationTokenDate());
    assertTrue(pending.getRoles().contains(userRole));
    verify(userRepository).save(pending);
    verify(mailService).sendCompleteUserMail(any());
  }

  @Test
  void verifyUserRegistration_keepsActivationWhenWelcomeMailFails() {
    UserEntity pending = pendingUser(LocalDateTime.now().plusMinutes(10));
    pending.setRoles(new HashSet<>());
    RoleEntity userRole = new RoleEntity();
    userRole.setName("USER");

    when(userRepository.findByCurrentVerificationTokenAndStatus("token", UserStatus.PENDING))
        .thenReturn(Optional.of(pending));
    when(roleService.getRoleByName("USER")).thenReturn(userRole);
    doThrow(new RuntimeException("smtp down")).when(mailService).sendCompleteUserMail(any());

    authService.verifyUserRegistration("token");

    assertEquals(UserStatus.ACTIVE, pending.getStatus());
    verify(userRepository).save(pending);
  }

  @Test
  void refreshUserVerification_ignoresUnknownUsername() {
    when(userRepository.findByUsernameAndStatusIn("ghost", List.of(UserStatus.PENDING)))
        .thenReturn(Optional.empty());

    authService.refreshUserVerification("ghost");

    verify(userRepository, never()).save(any());
  }

  @Test
  void refreshUserVerification_skipsWhenTokenStillValid() {
    UserEntity pending = pendingUser(LocalDateTime.now().plusMinutes(5));
    pending.setCurrentVerificationToken("existing");
    when(userRepository.findByUsernameAndStatusIn("bob", List.of(UserStatus.PENDING)))
        .thenReturn(Optional.of(pending));

    authService.refreshUserVerification("bob");

    verify(userRepository, never()).save(any());
    assertEquals("existing", pending.getCurrentVerificationToken());
  }

  @Test
  void refreshUserVerification_issuesNewTokenWhenExpired() {
    UserEntity pending = pendingUser(LocalDateTime.now().minusMinutes(1));
    pending.setCurrentVerificationToken("old");
    when(userRepository.findByUsernameAndStatusIn("bob", List.of(UserStatus.PENDING)))
        .thenReturn(Optional.of(pending));

    authService.refreshUserVerification("bob");

    verify(userRepository).save(pending);
    assertNotNull(pending.getCurrentVerificationToken());
    assertTrue(pending.getExpiredVerificationTokenDate().isAfter(LocalDateTime.now()));
  }

  private static LoginRequestDTO loginRequest(String username, String password) {
    LoginRequestDTO dto = new LoginRequestDTO();
    dto.setUsername(username);
    dto.setPassword(password);
    return dto;
  }

  private static RegistrationDTO registration(String username, String email) {
    RegistrationDTO dto = new RegistrationDTO();
    dto.setUsername(username);
    dto.setPassword("Password1");
    dto.setName("Bob");
    dto.setEmail(email);
    return dto;
  }

  private static UserEntity pendingUser(LocalDateTime expiry) {
    return UserEntity.builder()
        .username("bob")
        .email("bob@example.com")
        .name("Bob")
        .status(UserStatus.PENDING)
        .currentVerificationToken("token")
        .expiredVerificationTokenDate(expiry)
        .roles(Set.of())
        .build();
  }
}
