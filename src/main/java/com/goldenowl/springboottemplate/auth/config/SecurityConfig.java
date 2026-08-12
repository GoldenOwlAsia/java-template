package com.goldenowl.springboottemplate.auth.config;

import com.goldenowl.springboottemplate.app.constant.ProfileConstant;
import com.goldenowl.springboottemplate.auth.filter.JwtBlacklistFilter;
import com.goldenowl.springboottemplate.auth.handler.CustomAuthenticationFailureHandler;
import com.goldenowl.springboottemplate.auth.handler.OAuth2LoginSuccessHandler;
import com.goldenowl.springboottemplate.auth.service.CustomOAuth2UserService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

  private final CustomOAuth2UserService customOAuth2UserService;

  private final OAuth2LoginSuccessHandler oauth2LoginSuccessHandler;

  private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

  private final SecretKey jwtSecretKey;

  private final JwtBlacklistFilter jwtBlacklistFilter;

  private final Environment environment;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            auth -> {
              auth.requestMatchers(
                      "/",
                      "/api/v1/auth/**",
                      "/actuator/health",
                      "/actuator/health/**",
                      "/oauth2/**",
                      "/login/oauth2/code/google")
                  .permitAll();

              if (!environment.acceptsProfiles(Profiles.of(ProfileConstant.PRODUCTION))) {
                auth.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html")
                    .permitAll();
              }

              auth.requestMatchers("/api/debug/**").hasRole("ADMIN").anyRequest().authenticated();
            })
        .addFilterBefore(jwtBlacklistFilter, BearerTokenAuthenticationFilter.class)
        .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
        .oauth2ResourceServer(
            oauth2 ->
                oauth2.jwt(
                    jwtConfigurer ->
                        jwtConfigurer
                            .decoder(customJwtDecoder())
                            .jwtAuthenticationConverter(jwtAuthenticationConverter())))
        .oauth2Login(
            oauth2 ->
                oauth2
                    .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                    .successHandler(oauth2LoginSuccessHandler)
                    .failureHandler(customAuthenticationFailureHandler));
    return http.build();
  }

  @Bean
  public JwtDecoder customJwtDecoder() {
    return NimbusJwtDecoder.withSecretKey(jwtSecretKey).build();
  }

  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter =
        new JwtGrantedAuthoritiesConverter();
    grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
    grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");

    JwtAuthenticationConverter authConverter = new JwtAuthenticationConverter();
    authConverter.setJwtGrantedAuthoritiesConverter(
        jwt -> {
          Collection<GrantedAuthority> authorities =
              new ArrayList<>(grantedAuthoritiesConverter.convert(jwt));

          Optional.ofNullable(jwt.getClaimAsStringList("permissions"))
              .ifPresent(
                  permissions ->
                      permissions.forEach(p -> authorities.add(new SimpleGrantedAuthority(p))));

          return authorities;
        });

    return authConverter;
  }
}
