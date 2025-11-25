package com.example.caiwu.repository;

import com.example.caiwu.entity.Role;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    Optional<Role> findByNameIgnoreCase(String name);
    Set<Role> findByNameIn(Collection<String> names);
}
