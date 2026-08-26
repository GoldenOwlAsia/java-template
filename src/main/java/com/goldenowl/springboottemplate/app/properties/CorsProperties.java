package com.goldenowl.springboottemplate.app.properties;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "goldenowl.cors")
@Data
public class CorsProperties {

  private List<String> allowedOrigins = new ArrayList<>();

  private List<String> allowedMethods = List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");

  private List<String> allowedHeaders = List.of("*");

  private boolean allowCredentials = true;

  private long maxAge = 3600;
}
