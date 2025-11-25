package com.example.caiwu.controller;

import com.example.caiwu.dto.ApiResponse;
import com.example.caiwu.dto.AuthResponse;
import com.example.caiwu.dto.LoginRequest;
import com.example.caiwu.dto.RegisterRequest;
import com.example.caiwu.dto.UserResponse;
import com.example.caiwu.mapper.UserMapper;
import com.example.caiwu.repository.UserRepository;
import com.example.caiwu.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("注册成功", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("登录成功", response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> currentUser(Authentication authentication) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未认证");
        }
        return userRepository.findByUsername(authentication.getName())
                .map(UserMapper::toResponse)
                .map(user -> ResponseEntity.ok(ApiResponse.success(user)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));
    }
}
