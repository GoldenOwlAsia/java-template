package com.goldenowl.springboottemplate.user.mapper;

import com.goldenowl.springboottemplate.auth.entity.SignUpEntity;
import com.goldenowl.springboottemplate.auth.mapper.RoleMapper;
import com.goldenowl.springboottemplate.user.dto.UserProfileDTO;
import com.goldenowl.springboottemplate.user.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(uses = {RoleMapper.class})
public interface UserMapper {

    UserProfileDTO mapToProfileDTO(UserEntity entity);
    UserEntity mapToEntity(SignUpEntity signUpEntity);
}
