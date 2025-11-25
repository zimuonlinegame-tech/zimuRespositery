package com.example.caiwu.repository;

import com.example.caiwu.entity.Menu;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findByEnabledTrueOrderBySortOrderAscNameAsc();
}
