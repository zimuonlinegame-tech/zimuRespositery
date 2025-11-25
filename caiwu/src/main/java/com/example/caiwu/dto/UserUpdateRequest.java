package com.example.caiwu.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.util.Set;

public class UserUpdateRequest {

    @Email(message = "邮箱格式不正确")
    private String email;

    @Size(min = 6, message = "密码至少 6 位")
    private String password;

    private Boolean enabled;

    private Set<String> roles;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
