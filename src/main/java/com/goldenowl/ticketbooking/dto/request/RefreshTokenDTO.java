package com.goldenowl.ticketbooking.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenDTO(@NotBlank String refreshToken) {
}
