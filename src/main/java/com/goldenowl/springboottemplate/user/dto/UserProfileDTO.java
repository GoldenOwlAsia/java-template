package com.goldenowl.springboottemplate.user.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class UserProfileDTO {

  private String id;

  private String username;

  private String email;

  private String name;

  private List<String> roles;

  private LocalDateTime createdAt;
}
