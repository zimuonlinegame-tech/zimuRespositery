package com.example.caiwu.config;

import com.example.caiwu.entity.Menu;
import com.example.caiwu.entity.Permission;
import com.example.caiwu.entity.Role;
import com.example.caiwu.entity.User;
import com.example.caiwu.repository.MenuRepository;
import com.example.caiwu.repository.PermissionRepository;
import com.example.caiwu.repository.RoleRepository;
import com.example.caiwu.repository.UserRepository;
import com.example.caiwu.security.RbacConstants;
import java.util.Set;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class StartupDataLoader implements ApplicationRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;
    private final PasswordEncoder passwordEncoder;

    public StartupDataLoader(PermissionRepository permissionRepository,
                             RoleRepository roleRepository,
                             UserRepository userRepository,
                             MenuRepository menuRepository,
                             PasswordEncoder passwordEncoder) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.menuRepository = menuRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        Permission read = ensurePermission(RbacConstants.PERM_USER_READ);
        Permission create = ensurePermission(RbacConstants.PERM_USER_CREATE);
        Permission update = ensurePermission(RbacConstants.PERM_USER_UPDATE);
        Permission delete = ensurePermission(RbacConstants.PERM_USER_DELETE);

        Role adminRole = ensureRole(RbacConstants.ROLE_ADMIN, Set.of(read, create, update, delete));
        ensureRole(RbacConstants.ROLE_USER, Set.of(read));

        userRepository.findByUsername("admin").orElseGet(() -> {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setEnabled(true);
            admin.getRoles().add(adminRole);
            return userRepository.save(admin);
        });

        ensureMenu("用户管理", "/users", "users", 1);
    }

    private Permission ensurePermission(String name) {
        return permissionRepository.findByName(name)
                .orElseGet(() -> permissionRepository.save(new Permission(name)));
    }

    private Role ensureRole(String name, Set<Permission> permissions) {
        return roleRepository.findByNameIgnoreCase(name)
                .map(role -> {
                    role.setPermissions(permissions);
                    return roleRepository.save(role);
                })
                .orElseGet(() -> {
                    Role role = new Role(name);
                    role.setPermissions(permissions);
                    return roleRepository.save(role);
                });
    }

    private void ensureMenu(String name, String path, String icon, int sort) {
        menuRepository.findByEnabledTrueOrderBySortOrderAscNameAsc().stream()
                .filter(m -> name.equals(m.getName()))
                .findFirst()
                .orElseGet(() -> {
                    Menu menu = new Menu();
                    menu.setName(name);
                    menu.setPath(path);
                    menu.setIcon(icon);
                    menu.setSortOrder(sort);
                    return menuRepository.save(menu);
                });
    }
}
