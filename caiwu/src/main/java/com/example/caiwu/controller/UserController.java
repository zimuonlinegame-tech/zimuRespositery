package com.example.caiwu.controller;

import com.example.caiwu.dto.ApiResponse;
import com.example.caiwu.dto.PagedResponse;
import com.example.caiwu.dto.UserCreateRequest;
import com.example.caiwu.dto.UserResponse;
import com.example.caiwu.dto.UserUpdateRequest;
import com.example.caiwu.security.RbacConstants;
import com.example.caiwu.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('" + RbacConstants.PERM_USER_READ + "')")
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(ApiResponse.success(userService.listUsers(page, size, keyword)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('" + RbacConstants.PERM_USER_READ + "')")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUser(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('" + RbacConstants.PERM_USER_CREATE + "')")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("创建成功", userService.createUser(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('" + RbacConstants.PERM_USER_UPDATE + "')")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable Long id,
                                                                @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("更新成功", userService.updateUser(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('" + RbacConstants.PERM_USER_DELETE + "')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }
}
