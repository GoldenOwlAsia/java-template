package com.goldenowl.springboottemplate.auth.handler;

import com.goldenowl.springboottemplate.auth.dto.LoginResponseDTO;
import com.goldenowl.springboottemplate.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final JsonMapper jsonMapper;

  private final AuthService authService;

  public OAuth2LoginSuccessHandler(JsonMapper jsonMapper, @Lazy AuthService authService) {
    this.jsonMapper = jsonMapper;
    this.authService = authService;
  }

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException {
    response.setStatus(HttpServletResponse.SC_OK);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    final LoginResponseDTO loginResponse =
        authService.getLoginResponseWithAssignedTokens(authentication.getName());
    jsonMapper.writeValue(response.getWriter(), loginResponse);
  }
}
