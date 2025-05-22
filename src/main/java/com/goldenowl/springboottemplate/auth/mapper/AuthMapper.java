package com.goldenowl.springboottemplate.auth.mapper;

import com.goldenowl.springboottemplate.auth.dto.LoginResponseDTO;
import com.goldenowl.springboottemplate.auth.dto.RegistrationDTO;
import com.goldenowl.springboottemplate.auth.entity.SignUpEntity;
import com.goldenowl.springboottemplate.user.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(uses = {RoleMapper.class})
public interface AuthMapper {

    SignUpEntity map(RegistrationDTO registrationDTO, @MappingTarget SignUpEntity signUpEntity);

    LoginResponseDTO map(UserEntity user);
}
