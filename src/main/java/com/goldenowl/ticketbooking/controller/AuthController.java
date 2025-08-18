package com.goldenowl.ticketbooking.controller;

import com.goldenowl.ticketbooking.dto.request.LoginRequestDTO;
import com.goldenowl.ticketbooking.dto.response.LoginResponseDTO;
import com.goldenowl.ticketbooking.dto.request.RefreshTokenDTO;
import com.goldenowl.ticketbooking.dto.request.RegistrationDTO;
import com.goldenowl.ticketbooking.dto.response.TokenResponseDTO;
import com.goldenowl.ticketbooking.service.AuthService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return authService.login(loginRequestDTO);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    void logout(@Valid @RequestBody RefreshTokenDTO refreshTokenDTO, @RequestHeader("Authorization") String authHeader) {
        final String accessToken = authHeader.substring(7);
        final String refreshToken = refreshTokenDTO.refreshToken();
        authService.logout(accessToken, refreshToken);
    }

    @PostMapping("/refresh-token/{refreshToken}")
    @ResponseStatus(HttpStatus.OK)
    TokenResponseDTO refreshToken(@PathVariable String refreshToken) {
        return authService.refreshToken(refreshToken);
    }

    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.OK)
    void signUp(@Valid @RequestBody RegistrationDTO registrationDTO) {
        authService.signup(registrationDTO);
    }

    @PostMapping("/verification/{userVerifyToken}")
    @ResponseStatus(HttpStatus.OK)
    void verifyUser(@PathVariable String userVerifyToken) {
        authService.verifyUserRegistration(userVerifyToken);
    }

    @PostMapping("/refresh-user-verification")
    @ResponseStatus(HttpStatus.OK)
    void refreshUserVerification(@PathParam("username") String username) {
        authService.refreshUserVerification(username);
    }
}
