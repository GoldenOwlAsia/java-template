package com.goldenowl.springboottemplate.auth.service;

import com.goldenowl.springboottemplate.auth.entity.RoleEntity;

public interface RoleService {

    RoleEntity getRoleByName(String name);
}
