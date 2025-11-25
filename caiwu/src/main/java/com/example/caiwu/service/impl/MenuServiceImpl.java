package com.example.caiwu.service.impl;

import com.example.caiwu.dto.MenuResponse;
import com.example.caiwu.entity.Menu;
import com.example.caiwu.repository.MenuRepository;
import com.example.caiwu.service.MenuService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;

    public MenuServiceImpl(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    @Override
    public List<MenuResponse> listEnabledMenus() {
        return menuRepository.findByEnabledTrueOrderBySortOrderAscNameAsc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private MenuResponse toResponse(Menu menu) {
        MenuResponse response = new MenuResponse();
        response.setId(menu.getId());
        response.setName(menu.getName());
        response.setPath(menu.getPath());
        response.setIcon(menu.getIcon());
        response.setSortOrder(menu.getSortOrder());
        return response;
    }
}
