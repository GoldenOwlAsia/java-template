package com.goldenowl.springboottemplate.auth.config;

import com.goldenowl.springboottemplate.auth.properties.JwtProperties;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {

  private static final int MIN_SECRET_BYTES = 32;

  @Bean
  SecretKey jwtSecretKey(JwtProperties jwtProperties) {
    String secret = jwtProperties.getSecretKey();
    if (secret == null || secret.isBlank()) {
      throw new IllegalStateException("goldenowl.jwt.secret-key / JWT_SECRET_KEY must be set");
    }
    byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
    if (keyBytes.length < MIN_SECRET_BYTES) {
      throw new IllegalStateException(
          "JWT secret must be at least " + MIN_SECRET_BYTES + " bytes for HS256");
    }
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
