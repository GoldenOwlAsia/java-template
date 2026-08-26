package com.goldenowl.springboottemplate.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenDTO(
    @NotBlank(message = "Refresh token must not be blank") String refreshToken) {}
