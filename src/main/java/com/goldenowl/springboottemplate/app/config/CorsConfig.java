package com.goldenowl.springboottemplate.app.config;

import com.goldenowl.springboottemplate.app.properties.CorsProperties;
import java.util.List;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableConfigurationProperties(CorsProperties.class)
public class CorsConfig {

  @Bean
  CorsConfigurationSource corsConfigurationSource(CorsProperties properties) {
    List<String> origins =
        properties.getAllowedOrigins().stream().filter(StringUtils::hasText).toList();

    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(origins);
    config.setAllowedMethods(properties.getAllowedMethods());
    config.setAllowedHeaders(properties.getAllowedHeaders());
    config.setAllowCredentials(properties.isAllowCredentials());
    config.setMaxAge(properties.getMaxAge());

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
