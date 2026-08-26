package com.goldenowl.springboottemplate.user.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.goldenowl.springboottemplate.app.config.SecurityConfigTest;
import com.goldenowl.springboottemplate.app.constant.ProfileConstant;
import com.goldenowl.springboottemplate.app.exception.GlobalExceptionHandler;
import com.goldenowl.springboottemplate.auth.service.TokenBlacklistService;
import com.goldenowl.springboottemplate.user.dto.UserProfileDTO;
import com.goldenowl.springboottemplate.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@ActiveProfiles(ProfileConstant.TEST)
@Import({SecurityConfigTest.class, GlobalExceptionHandler.class})
class UserControllerTest {

  private static final String BASE = "/api/v1/users";

  @Autowired private MockMvc mockMvc;

  @MockitoBean private UserService userService;

  @MockitoBean private TokenBlacklistService tokenBlacklistService;

  @Test
  @WithMockUser(username = "bob")
  void getProfile_allowsSelf() throws Exception {
    UserProfileDTO profile = new UserProfileDTO();
    profile.setUsername("bob");
    when(userService.getProfileByUsername("bob")).thenReturn(profile);

    mockMvc
        .perform(get(BASE + "/bob"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("bob"));

    verify(userService).getProfileByUsername("bob");
  }

  @Test
  @WithMockUser(username = "alice")
  void getProfile_forbidsOtherUser() throws Exception {
    mockMvc.perform(get(BASE + "/bob")).andExpect(status().isForbidden());
    verifyNoInteractions(userService);
  }

  @Test
  @WithMockUser(username = "admin", roles = "ADMIN")
  void getProfile_allowsAdmin() throws Exception {
    UserProfileDTO profile = new UserProfileDTO();
    profile.setUsername("bob");
    when(userService.getProfileByUsername("bob")).thenReturn(profile);

    mockMvc.perform(get(BASE + "/bob")).andExpect(status().isOk());
    verify(userService).getProfileByUsername("bob");
  }

  @Test
  @WithMockUser(username = "admin", roles = "ADMIN")
  void deleteUser_allowsAdmin() throws Exception {
    mockMvc.perform(delete(BASE + "/bob")).andExpect(status().isOk());
    verify(userService).deleteUser("bob");
  }

  @Test
  @WithMockUser(username = "bob")
  void deleteUser_forbidsNonAdmin() throws Exception {
    mockMvc.perform(delete(BASE + "/bob")).andExpect(status().isForbidden());
    verifyNoInteractions(userService);
  }
}
