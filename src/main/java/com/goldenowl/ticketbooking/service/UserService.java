package com.goldenowl.ticketbooking.service;

import com.goldenowl.ticketbooking.entity.SignUpEntity;
import com.goldenowl.ticketbooking.dto.request.UserProfileDTO;
import com.goldenowl.ticketbooking.entity.UserEntity;

public interface UserService {

    UserProfileDTO getProfileByUsername(String username);

    UserEntity getUserByUsername(String username);

    void createUser(SignUpEntity signUpEntity);

    void deleteUser(String id);
}
