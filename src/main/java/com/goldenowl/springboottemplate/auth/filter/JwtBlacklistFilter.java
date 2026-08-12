package com.goldenowl.springboottemplate.auth.filter;

import com.goldenowl.springboottemplate.app.dto.ErrorResponseDTO;
import com.goldenowl.springboottemplate.auth.service.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.json.JsonMapper;

@Component
@RequiredArgsConstructor
public class JwtBlacklistFilter extends OncePerRequestFilter {

  private final TokenBlacklistService tokenBlacklistService;

  private final JsonMapper jsonMapper;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String accessToken = authHeader.substring(7);
      if (tokenBlacklistService.isAccessTokenBlacklisted(accessToken)) {
        writeError(response, HttpStatus.UNAUTHORIZED, "Access token has been blacklisted", request);
        return;
      }
    }

    filterChain.doFilter(request, response);
  }

  private void writeError(
      HttpServletResponse response, HttpStatus status, String message, HttpServletRequest request)
      throws IOException {
    response.setStatus(status.value());
    response.setCharacterEncoding("UTF-8");
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    jsonMapper.writeValue(
        response.getWriter(),
        ErrorResponseDTO.builder()
            .status(status.value())
            .message(message)
            .path(request.getRequestURI())
            .build());
  }
}
