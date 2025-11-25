package com.example.caiwu.service;

import com.example.caiwu.dto.MenuResponse;
import java.util.List;

public interface MenuService {
    List<MenuResponse> listEnabledMenus();
}
