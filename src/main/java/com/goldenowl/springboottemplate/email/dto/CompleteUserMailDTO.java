package com.goldenowl.springboottemplate.email.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompleteUserMailDTO {

  private String email;

  private String name;

  private String username;

  private LocalDateTime createdAt;
}
