package com.goldenowl.springboottemplate.auth.service.impl;

import com.goldenowl.springboottemplate.app.exception.ResourceNotFoundException;
import com.goldenowl.springboottemplate.auth.entity.RoleEntity;
import com.goldenowl.springboottemplate.auth.repository.RoleRepository;
import com.goldenowl.springboottemplate.auth.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public RoleEntity getRoleByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Role %s not found".formatted(name)));
    }
}
