package com.goldenowl.springboottemplate.auth.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.goldenowl.springboottemplate.app.exception.ResourceNotFoundException;
import com.goldenowl.springboottemplate.auth.entity.RoleEntity;
import com.goldenowl.springboottemplate.auth.repository.RoleRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

  @Mock private RoleRepository roleRepository;

  @InjectMocks private RoleServiceImpl roleService;

  @Test
  void getRoleByName_returnsRole() {
    RoleEntity role = new RoleEntity();
    role.setName("ADMIN");
    when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(role));

    assertEquals(role, roleService.getRoleByName("ADMIN"));
  }

  @Test
  void getRoleByName_throwsWhenMissing() {
    when(roleRepository.findByName("GHOST")).thenReturn(Optional.empty());

    ResourceNotFoundException ex =
        assertThrows(ResourceNotFoundException.class, () -> roleService.getRoleByName("GHOST"));
    assertEquals("Role GHOST not found", ex.getMessage());
  }
}
