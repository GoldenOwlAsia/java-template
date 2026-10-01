package com.goldenowl.springboottemplate.auth.mapper;

import com.goldenowl.springboottemplate.auth.dto.LoginResponseDTO;
import com.goldenowl.springboottemplate.auth.dto.RegistrationDTO;
import com.goldenowl.springboottemplate.user.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(uses = {RoleMapper.class})
public interface AuthMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "ol", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "lastModifiedAt", ignore = true)
  @Mapping(target = "lastModifiedBy", ignore = true)
  @Mapping(target = "oauthId", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "currentVerificationToken", ignore = true)
  @Mapping(target = "expiredVerificationTokenDate", ignore = true)
  @Mapping(target = "roles", ignore = true)
  @Mapping(target = "password", ignore = true)
  UserEntity mapToEntity(RegistrationDTO registrationDTO, @MappingTarget UserEntity userEntity);

  LoginResponseDTO mapToLoginResponseDTO(UserEntity user);
}
