package com.goldenowl.springboottemplate.email.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerifyUserMailDTO {

  private String email;

  private String name;

  private LocalDateTime expiredDate;

  private String verifyToken;
}
