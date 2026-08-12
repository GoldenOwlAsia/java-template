package com.goldenowl.springboottemplate.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.goldenowl.springboottemplate.auth.entity.CustomOAuth2User;
import com.goldenowl.springboottemplate.auth.entity.RoleEntity;
import com.goldenowl.springboottemplate.user.entity.UserEntity;
import com.goldenowl.springboottemplate.user.enumeration.UserStatus;
import com.goldenowl.springboottemplate.user.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

@ExtendWith(MockitoExtension.class)
class CustomOAuth2UserServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private RoleService roleService;

  @Mock private PasswordEncoder passwordEncoder;

  private OAuth2UserRequest userRequest;

  private OAuth2User oauth2User;

  @BeforeEach
  void setUp() {
    ClientRegistration registration =
        ClientRegistration.withRegistrationId("google")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .clientId("client")
            .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
            .authorizationUri("https://example.com/auth")
            .tokenUri("https://example.com/token")
            .userInfoUri("https://example.com/userinfo")
            .userNameAttributeName("sub")
            .build();

    OAuth2AccessToken accessToken =
        new OAuth2AccessToken(
            OAuth2AccessToken.TokenType.BEARER,
            "token-value",
            Instant.now(),
            Instant.now().plusSeconds(3600));

    userRequest = new OAuth2UserRequest(registration, accessToken);
    oauth2User =
        new DefaultOAuth2User(
            List.of(),
            Map.of("sub", "subject-1", "email", "alice@example.com", "name", "Alice"),
            "sub");
  }

  @Test
  void loadUser_createsNewUserWhenEmailUnknown() {
    RoleEntity role = new RoleEntity();
    role.setName("USER");

    withMockedDelegate(
        service -> {
          when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.empty());
          when(roleService.getRoleByName("USER")).thenReturn(role);
          when(passwordEncoder.encode(anyString())).thenReturn("encoded");
          when(userRepository.save(any(UserEntity.class)))
              .thenAnswer(invocation -> invocation.getArgument(0));

          OAuth2User result = service.loadUser(userRequest);

          assertInstanceOf(CustomOAuth2User.class, result);
          assertEquals("alice@example.com", result.getName());

          ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
          verify(userRepository).save(captor.capture());
          UserEntity saved = captor.getValue();
          assertEquals(UserStatus.ACTIVE, saved.getStatus());
          assertEquals("google:subject-1", saved.getOauthId());
          assertEquals(Set.of(role), saved.getRoles());
        });
  }

  @Test
  void loadUser_activatesExistingPendingUserAndUpdatesOauthId() {
    UserEntity existing =
        UserEntity.builder()
            .username("alice@example.com")
            .email("alice@example.com")
            .name("Alice")
            .status(UserStatus.PENDING)
            .oauthId(null)
            .build();

    withMockedDelegate(
        service -> {
          when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(existing));
          when(userRepository.save(existing)).thenReturn(existing);

          OAuth2User result = service.loadUser(userRequest);

          assertEquals("alice@example.com", result.getName());
          assertEquals(UserStatus.ACTIVE, existing.getStatus());
          assertEquals("google:subject-1", existing.getOauthId());
          verify(userRepository).save(existing);
          verify(roleService, never()).getRoleByName(anyString());
        });
  }

  @Test
  void loadUser_skipsSaveWhenUserAlreadyActiveWithSameOauthId() {
    UserEntity existing =
        UserEntity.builder()
            .username("alice@example.com")
            .email("alice@example.com")
            .name("Alice")
            .status(UserStatus.ACTIVE)
            .oauthId("google:subject-1")
            .build();

    withMockedDelegate(
        service -> {
          when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(existing));

          service.loadUser(userRequest);

          verify(userRepository, never()).save(any());
        });
  }

  private void withMockedDelegate(Consumer<CustomOAuth2UserService> test) {
    try (MockedConstruction<DefaultOAuth2UserService> ignored =
        mockConstruction(
            DefaultOAuth2UserService.class,
            (mock, context) -> when(mock.loadUser(userRequest)).thenReturn(oauth2User))) {
      test.accept(new CustomOAuth2UserService(userRepository, roleService, passwordEncoder));
    }
  }
}
