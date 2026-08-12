package com.goldenowl.springboottemplate.auth.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.goldenowl.springboottemplate.auth.dto.LoginResponseDTO;
import com.goldenowl.springboottemplate.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import tools.jackson.databind.json.JsonMapper;

@ExtendWith(MockitoExtension.class)
class OAuth2LoginSuccessHandlerTest {

  @Mock private AuthService authService;

  @Mock private HttpServletRequest request;

  @Mock private Authentication authentication;

  private MockHttpServletResponse response;

  private OAuth2LoginSuccessHandler handler;

  @BeforeEach
  void setUp() {
    response = new MockHttpServletResponse();
    handler = new OAuth2LoginSuccessHandler(JsonMapper.builder().build(), authService);
  }

  @Test
  void onAuthenticationSuccess_writesLoginJson() throws Exception {
    LoginResponseDTO loginResponse = new LoginResponseDTO();
    loginResponse.setUsername("alice");
    loginResponse.setToken("access");
    when(authentication.getName()).thenReturn("alice");
    when(authService.getLoginResponseWithAssignedTokens("alice")).thenReturn(loginResponse);

    handler.onAuthenticationSuccess(request, response, authentication);

    assertEquals(200, response.getStatus());
    assertTrue(response.getContentAsString().contains("alice"));
    assertTrue(response.getContentAsString().contains("access"));
    verify(authService).getLoginResponseWithAssignedTokens("alice");
  }
}
