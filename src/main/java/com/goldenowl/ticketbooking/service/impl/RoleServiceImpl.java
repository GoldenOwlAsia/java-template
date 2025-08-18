package com.goldenowl.ticketbooking.service.impl;

import com.goldenowl.ticketbooking.exception.ResourceNotFoundException;
import com.goldenowl.ticketbooking.entity.RoleEntity;
import com.goldenowl.ticketbooking.repository.RoleRepository;
import com.goldenowl.ticketbooking.service.RoleService;
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
