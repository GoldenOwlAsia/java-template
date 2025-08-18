package com.goldenowl.ticketbooking.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenResponseDTO {
    private String accessToken;
    private String refreshToken;
}
