package com.goldenowl.springboottemplate.auth.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.goldenowl.springboottemplate.auth.service.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import tools.jackson.databind.json.JsonMapper;

@ExtendWith(MockitoExtension.class)
class JwtBlacklistFilterTest {

  @Mock private TokenBlacklistService tokenBlacklistService;

  @Mock private HttpServletRequest request;

  @Mock private FilterChain filterChain;

  private MockHttpServletResponse response;

  private JwtBlacklistFilter filter;

  @BeforeEach
  void setUp() {
    response = new MockHttpServletResponse();
    filter = new JwtBlacklistFilter(tokenBlacklistService, JsonMapper.builder().build());
  }

  @Test
  void doFilter_continuesWhenNoAuthorizationHeader() throws Exception {
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(tokenBlacklistService, never()).isAccessTokenBlacklisted(any());
  }

  @Test
  void doFilter_continuesWhenHeaderIsNotBearer() throws Exception {
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic abc");

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
  }

  @Test
  void doFilter_continuesWhenTokenNotBlacklisted() throws Exception {
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer access");
    when(tokenBlacklistService.isAccessTokenBlacklisted("access")).thenReturn(false);

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
  }

  @Test
  void doFilter_rejectsBlacklistedAccessToken() throws Exception {
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer access");
    when(request.getRequestURI()).thenReturn("/api/v1/articles");
    when(tokenBlacklistService.isAccessTokenBlacklisted("access")).thenReturn(true);

    filter.doFilterInternal(request, response, filterChain);

    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    assertTrue(response.getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));
    assertTrue(response.getContentAsString().contains("Access token has been blacklisted"));
    verify(filterChain, never())
        .doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class));
  }
}
