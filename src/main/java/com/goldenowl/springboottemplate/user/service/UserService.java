package com.goldenowl.springboottemplate.user.service;

import com.goldenowl.springboottemplate.user.dto.UserProfileDTO;
import com.goldenowl.springboottemplate.user.entity.UserEntity;

public interface UserService {

  UserProfileDTO getProfileByUsername(String username);

  UserEntity getUserByUsername(String username);

  void deleteUser(String username);
}
