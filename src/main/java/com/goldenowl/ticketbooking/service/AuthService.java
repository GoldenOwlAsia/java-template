package com.goldenowl.ticketbooking.service;

import com.goldenowl.ticketbooking.dto.request.LoginRequestDTO;
import com.goldenowl.ticketbooking.dto.response.LoginResponseDTO;
import com.goldenowl.ticketbooking.dto.request.RegistrationDTO;
import com.goldenowl.ticketbooking.dto.response.TokenResponseDTO;
import com.goldenowl.ticketbooking.entity.UserEntity;

public interface AuthService {

    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);

    void logout(String accessToken, String refreshToken);

    TokenResponseDTO refreshToken(String refreshToken);

    LoginResponseDTO getLoginResponseWithAssignedTokens(UserEntity userEntity);

    void signup(RegistrationDTO registrationDTO);

    void verifyUserRegistration(String token);

    void refreshUserVerification(String username);
}
