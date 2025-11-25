package com.example.caiwu.service;

import com.example.caiwu.dto.AuthResponse;
import com.example.caiwu.dto.LoginRequest;
import com.example.caiwu.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
