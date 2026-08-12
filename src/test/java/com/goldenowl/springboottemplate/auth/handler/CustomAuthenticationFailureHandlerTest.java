package com.goldenowl.springboottemplate.auth.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import tools.jackson.databind.json.JsonMapper;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationFailureHandlerTest {

  @Mock private HttpServletRequest request;

  private MockHttpServletResponse response;

  private CustomAuthenticationFailureHandler handler;

  @BeforeEach
  void setUp() {
    response = new MockHttpServletResponse();
    handler = new CustomAuthenticationFailureHandler(JsonMapper.builder().build());
  }

  @Test
  void onAuthenticationFailure_returnsUnauthorizedJson() throws Exception {
    handler.onAuthenticationFailure(request, response, new BadCredentialsException("bad"));

    assertEquals(401, response.getStatus());
    assertTrue(response.getContentAsString().contains("OAuth2 authentication failed"));
  }
}
