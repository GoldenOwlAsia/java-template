package com.goldenowl.ticketbooking.dto.request;

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
