package com.goldenowl.ticketbooking.mapper;

import com.goldenowl.ticketbooking.dto.response.LoginResponseDTO;
import com.goldenowl.ticketbooking.dto.request.RegistrationDTO;
import com.goldenowl.ticketbooking.entity.SignUpEntity;
import com.goldenowl.ticketbooking.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(uses = {RoleMapper.class})
public interface AuthMapper {

    SignUpEntity map(RegistrationDTO registrationDTO, @MappingTarget SignUpEntity signUpEntity);

    LoginResponseDTO map(UserEntity user);
}
