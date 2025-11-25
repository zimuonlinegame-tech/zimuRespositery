package com.example.caiwu.controller;

import com.example.caiwu.dto.ApiResponse;
import com.example.caiwu.dto.MenuResponse;
import com.example.caiwu.service.MenuService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/menus")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<MenuResponse>>> listMenus() {
        return ResponseEntity.ok(ApiResponse.success(menuService.listEnabledMenus()));
    }
}
