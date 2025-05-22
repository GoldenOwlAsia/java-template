package com.goldenowl.springboottemplate.email.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class VerifyUserMailDTO {
    private String email;
    private String name;
    private LocalDateTime expiredDate;
    private String verifyToken;
}
