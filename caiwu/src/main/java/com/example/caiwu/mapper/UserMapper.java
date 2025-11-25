package com.example.caiwu.mapper;

import com.example.caiwu.dto.UserResponse;
import com.example.caiwu.entity.Permission;
import com.example.caiwu.entity.Role;
import com.example.caiwu.entity.User;
import java.util.Set;
import java.util.stream.Collectors;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setEnabled(user.isEnabled());
        response.setCreatedAt(user.getCreatedAt());
        response.setRoles(mapRoles(user.getRoles()));
        response.setPermissions(mapPermissions(user.getRoles()));
        return response;
    }

    private static Set<String> mapRoles(Set<Role> roles) {
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    private static Set<String> mapPermissions(Set<Role> roles) {
        return roles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .collect(Collectors.toSet());
    }
}
