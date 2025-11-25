package com.example.caiwu.security;

import com.example.caiwu.entity.Permission;
import com.example.caiwu.entity.Role;
import com.example.caiwu.entity.User;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final boolean enabled;
    private final Set<GrantedAuthority> authorities;

    private UserPrincipal(Long id, String username, String password, boolean enabled, Set<GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.authorities = authorities;
    }

    public static UserPrincipal fromUser(User user) {
        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .flatMap(role -> mapAuthorities(role).stream())
                .collect(Collectors.toSet());
        return new UserPrincipal(user.getId(), user.getUsername(), user.getPassword(), user.isEnabled(), authorities);
    }

    private static Set<GrantedAuthority> mapAuthorities(Role role) {
        Set<GrantedAuthority> authorities = role.getPermissions().stream()
                .map(Permission::getName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        return authorities;
    }

    public Long getId() {
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
