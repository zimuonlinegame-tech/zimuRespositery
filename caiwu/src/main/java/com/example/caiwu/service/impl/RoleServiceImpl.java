package com.example.caiwu.service.impl;

import com.example.caiwu.dto.RoleResponse;
import com.example.caiwu.entity.Role;
import com.example.caiwu.mapper.RoleMapper;
import com.example.caiwu.repository.RoleRepository;
import com.example.caiwu.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(RoleMapper::toResponse)
                .collect(Collectors.toList());
    }
}