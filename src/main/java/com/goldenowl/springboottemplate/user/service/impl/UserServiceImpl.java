package com.goldenowl.springboottemplate.user.service.impl;

import com.goldenowl.springboottemplate.app.exception.ResourceNotFoundException;
import com.goldenowl.springboottemplate.user.dto.UserProfileDTO;
import com.goldenowl.springboottemplate.user.entity.UserEntity;
import com.goldenowl.springboottemplate.user.enumeration.UserStatus;
import com.goldenowl.springboottemplate.user.mapper.UserMapper;
import com.goldenowl.springboottemplate.user.repository.UserRepository;
import com.goldenowl.springboottemplate.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
class UserServiceImpl implements UserService {

  private final UserMapper userMapper;

  private final UserRepository userRepository;

  @Override
  public UserProfileDTO getProfileByUsername(String username) {
    log.info("Getting profile for username: {}", username);
    return userMapper.mapToProfileDTO(getUserByUsername(username));
  }

  @Override
  public UserEntity getUserByUsername(String username) {
    return userRepository
        .findByUsernameAndStatus(username, UserStatus.ACTIVE)
        .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
  }

  @Override
  public void deleteUser(String username) {
    log.info("Deleting user with username: {}", username);
    UserEntity userEntity = getUserByUsername(username);
    userRepository.delete(userEntity);
    log.info("User {} deleted (soft delete)", username);
  }
}
