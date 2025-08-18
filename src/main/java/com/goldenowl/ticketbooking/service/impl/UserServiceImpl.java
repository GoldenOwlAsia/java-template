package com.goldenowl.ticketbooking.service.impl;

import com.goldenowl.ticketbooking.exception.ResourceAlreadyExistsException;
import com.goldenowl.ticketbooking.exception.ResourceNotFoundException;
import com.goldenowl.ticketbooking.entity.RoleEntity;
import com.goldenowl.ticketbooking.entity.SignUpEntity;
import com.goldenowl.ticketbooking.enumeration.UserDefaultType;
import com.goldenowl.ticketbooking.service.RoleService;
import com.goldenowl.ticketbooking.dto.request.UserProfileDTO;
import com.goldenowl.ticketbooking.entity.UserEntity;
import com.goldenowl.ticketbooking.mapper.UserMapper;
import com.goldenowl.ticketbooking.repository.UserRepository;
import com.goldenowl.ticketbooking.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final RoleService roleService;

    @Override
    public UserProfileDTO getProfileByUsername(String username) {
        log.info("Getting profile for username: {}", username);
        UserEntity userEntity = getUserEntity(username);
        return userMapper.mapToProfileDTO(userEntity);
    }

    @Override
    public UserEntity getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username '%s' not found".
                        formatted(username)));
    }

    @Override
    public void createUser(SignUpEntity signUpEntity) {
        if (userRepository.existsByUsername(signUpEntity.getUsername())) {
            throw new ResourceAlreadyExistsException("User is already existed");
        }
        if (userRepository.existsByEmail(signUpEntity.getEmail())) {
            throw new ResourceAlreadyExistsException("Email is already existed");
        }

        // Save user info to database
        UserEntity userEntity = userMapper.mapToEntity(signUpEntity);
        RoleEntity userRole = roleService.getRoleByName(UserDefaultType.USER.name());
        userEntity.setRoles(Set.of(userRole));
        userEntity.setSignUp(signUpEntity);
        userRepository.save(userEntity);
    }

    @Override
    public void deleteUser(String username) {
        log.info("Deleting user with username: {}", username);
        UserEntity userEntity = getUserEntity(username);
        // Soft delete
        userRepository.delete(userEntity);
        log.info("User {} deleted (soft delete)", username);
    }

    private UserEntity getUserEntity(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException("User", "username", username)
        );
    }
}
