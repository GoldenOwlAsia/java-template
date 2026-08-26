package com.goldenowl.springboottemplate.user.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.goldenowl.springboottemplate.app.exception.ResourceNotFoundException;
import com.goldenowl.springboottemplate.user.dto.UserProfileDTO;
import com.goldenowl.springboottemplate.user.entity.UserEntity;
import com.goldenowl.springboottemplate.user.enumeration.UserStatus;
import com.goldenowl.springboottemplate.user.mapper.UserMapper;
import com.goldenowl.springboottemplate.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @Mock private UserMapper userMapper;

  @Mock private UserRepository userRepository;

  @InjectMocks private UserServiceImpl userService;

  @Test
  void getProfileByUsername_returnsMappedProfile() {
    UserEntity user = new UserEntity();
    user.setUsername("bob");
    UserProfileDTO profile = new UserProfileDTO();
    profile.setUsername("bob");

    when(userRepository.findByUsernameAndStatus("bob", UserStatus.ACTIVE))
        .thenReturn(Optional.of(user));
    when(userMapper.mapToProfileDTO(user)).thenReturn(profile);

    UserProfileDTO result = userService.getProfileByUsername("bob");

    assertEquals("bob", result.getUsername());
    verify(userMapper).mapToProfileDTO(user);
  }

  @Test
  void getUserByUsername_throwsWhenMissing() {
    when(userRepository.findByUsernameAndStatus("ghost", UserStatus.ACTIVE))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> userService.getUserByUsername("ghost"));
  }

  @Test
  void deleteUser_softDeletesActiveUser() {
    UserEntity user = new UserEntity();
    user.setUsername("bob");
    when(userRepository.findByUsernameAndStatus("bob", UserStatus.ACTIVE))
        .thenReturn(Optional.of(user));

    userService.deleteUser("bob");

    verify(userRepository).delete(user);
  }
}
