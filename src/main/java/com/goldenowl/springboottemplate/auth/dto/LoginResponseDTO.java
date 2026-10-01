package com.goldenowl.springboottemplate.auth.dto;

import java.util.List;
import lombok.Data;

@Data
public class LoginResponseDTO {

  private String username;

  private String email;

  private String name;

  private String oauthId;

  private List<String> roles;

  private String token;

  private String refreshToken;
}
