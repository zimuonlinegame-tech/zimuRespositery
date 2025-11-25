package com.example.caiwu.service;

import com.example.caiwu.dto.PagedResponse;
import com.example.caiwu.dto.UserCreateRequest;
import com.example.caiwu.dto.UserResponse;
import com.example.caiwu.dto.UserUpdateRequest;

public interface UserService {
    PagedResponse<UserResponse> listUsers(int page, int size, String keyword);
    UserResponse getUser(Long id);
    UserResponse createUser(UserCreateRequest request);
    UserResponse updateUser(Long id, UserUpdateRequest request);
    void deleteUser(Long id);
}
