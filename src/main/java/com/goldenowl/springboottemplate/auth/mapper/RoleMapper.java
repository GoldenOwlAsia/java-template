package com.goldenowl.springboottemplate.auth.mapper;

import com.goldenowl.springboottemplate.auth.entity.RoleEntity;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;

@Mapper
public interface RoleMapper {

  default List<String> mapToRoleNames(Set<RoleEntity> roles) {
    if (roles == null) {
      return List.of();
    }
    return roles.stream().map(RoleEntity::getName).collect(Collectors.toList());
  }
}
