package com.goldenowl.springboottemplate.email.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CompleteUserMailDTO {
    private String email;
    private String name;
    private String username;
    private LocalDateTime createdAt;
}
