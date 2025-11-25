package com.example.caiwu.mapper;

import com.example.caiwu.dto.RoleResponse;
import com.example.caiwu.entity.Role;

public final class RoleMapper {

    private RoleMapper() {
    }

    public static RoleResponse toResponse(Role role) {
        RoleResponse response = new RoleResponse();
        response.setId(role.getId());
        response.setName(role.getName());
        response.setDisplayName(role.getDisplayName());
        return response;
    }
}