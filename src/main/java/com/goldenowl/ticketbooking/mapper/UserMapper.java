package com.goldenowl.ticketbooking.mapper;

import com.goldenowl.ticketbooking.entity.SignUpEntity;
import com.goldenowl.ticketbooking.dto.request.UserProfileDTO;
import com.goldenowl.ticketbooking.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(uses = {RoleMapper.class})
public interface UserMapper {

    UserProfileDTO mapToProfileDTO(UserEntity entity);
    UserEntity mapToEntity(SignUpEntity signUpEntity);
}
