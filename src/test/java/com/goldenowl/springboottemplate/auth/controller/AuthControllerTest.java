package com.goldenowl.springboottemplate.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.goldenowl.springboottemplate.app.config.SecurityConfigTest;
import com.goldenowl.springboottemplate.app.constant.ProfileConstant;
import com.goldenowl.springboottemplate.app.exception.GlobalExceptionHandler;
import com.goldenowl.springboottemplate.auth.dto.LoginRequestDTO;
import com.goldenowl.springboottemplate.auth.dto.LoginResponseDTO;
import com.goldenowl.springboottemplate.auth.dto.RegistrationDTO;
import com.goldenowl.springboottemplate.auth.dto.TokenResponseDTO;
import com.goldenowl.springboottemplate.auth.service.AuthService;
import com.goldenowl.springboottemplate.auth.service.TokenBlacklistService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthController.class)
@ActiveProfiles(ProfileConstant.TEST)
@Import({SecurityConfigTest.class, GlobalExceptionHandler.class})
class AuthControllerTest {

  private static final String BASE = "/api/v1/auth";

  @Autowired private MockMvc mockMvc;

  @Autowired private JsonMapper jsonMapper;

  @MockitoBean private AuthService authService;

  @MockitoBean private TokenBlacklistService tokenBlacklistService;

  @Test
  void login_returnsOk() throws Exception {
    LoginResponseDTO response = new LoginResponseDTO();
    response.setUsername("admin");
    response.setToken("access");
    response.setRefreshToken("refresh");
    when(authService.login(any(LoginRequestDTO.class))).thenReturn(response);

    mockMvc
        .perform(
            post(BASE + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {"username":"admin","password":"secret"}
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("admin"))
        .andExpect(jsonPath("$.token").value("access"));

    verify(authService).login(any(LoginRequestDTO.class));
  }

  @Test
  void login_rejectsBlankFields() throws Exception {
    mockMvc
        .perform(
            post(BASE + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {"username":"","password":""}
                """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Validation failed"));
  }

  @Test
  void logout_delegatesWithBearerToken() throws Exception {
    mockMvc
        .perform(
            post(BASE + "/logout")
                .header("Authorization", "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {"refreshToken":"refresh-token"}
                        """))
        .andExpect(status().isOk());

    verify(authService).logout("access-token", "refresh-token");
  }

  @Test
  void logout_requiresBearerAuthorization() throws Exception {
    mockMvc
        .perform(
            post(BASE + "/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {"refreshToken":"refresh-token"}
                """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Authorization Bearer token is required"));
  }

  @Test
  void refreshToken_returnsNewTokens() throws Exception {
    when(authService.refreshToken("refresh-token"))
        .thenReturn(
            TokenResponseDTO.builder()
                .accessToken("new-access")
                .refreshToken("new-refresh")
                .build());

    mockMvc
        .perform(
            post(BASE + "/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {"refreshToken":"refresh-token"}
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value("new-access"))
        .andExpect(jsonPath("$.refreshToken").value("new-refresh"));
  }

  @Test
  void signUp_returnsOk() throws Exception {
    mockMvc
        .perform(
            post(BASE + "/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(validRegistration())))
        .andExpect(status().isOk());

    verify(authService).signup(any(RegistrationDTO.class));
  }

  @Test
  void verifyUser_returnsOk() throws Exception {
    mockMvc.perform(post(BASE + "/verification/verify-token")).andExpect(status().isOk());

    verify(authService).verifyUserRegistration("verify-token");
  }

  @Test
  void refreshUserVerification_returnsOk() throws Exception {
    mockMvc
        .perform(post(BASE + "/refresh-user-verification").param("username", "bob"))
        .andExpect(status().isOk());

    verify(authService).refreshUserVerification("bob");
  }

  private static RegistrationDTO validRegistration() {
    RegistrationDTO dto = new RegistrationDTO();
    dto.setUsername("bob_user");
    dto.setPassword("Password1");
    dto.setName("Bob");
    dto.setEmail("bob@example.com");
    return dto;
  }
}
