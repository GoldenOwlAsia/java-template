package com.goldenowl.springboottemplate.auth.properties;

import java.time.Duration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "goldenowl.jwt")
@Data
public class JwtProperties {

  private Duration tokenExp;

  private Duration refreshTokenExp;

  private String secretKey;
}
