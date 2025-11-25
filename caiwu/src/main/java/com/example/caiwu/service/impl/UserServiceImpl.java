package com.example.caiwu.service.impl;

import com.example.caiwu.dto.PagedResponse;
import com.example.caiwu.dto.UserCreateRequest;
import com.example.caiwu.dto.UserResponse;
import com.example.caiwu.dto.UserUpdateRequest;
import com.example.caiwu.entity.Role;
import com.example.caiwu.entity.User;
import com.example.caiwu.mapper.UserMapper;
import com.example.caiwu.repository.RoleRepository;
import com.example.caiwu.repository.UserRepository;
import com.example.caiwu.security.RbacConstants;
import com.example.caiwu.service.UserService;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<UserResponse> listUsers(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<User> result;
        if (StringUtils.hasText(keyword)) {
            result = userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, pageable);
        } else {
            result = userRepository.findAll(pageable);
        }
        List<UserResponse> content = result.getContent().stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
        return new PagedResponse<>(content, result.getTotalElements(), result.getTotalPages(), page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));
        return UserMapper.toResponse(user);
    }

    @Override
    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "用户名已存在");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "邮箱已存在");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(request.isEnabled());
        user.setRoles(loadRoles(request.getRoles()));
        return UserMapper.toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));
        if (StringUtils.hasText(request.getEmail()) && !request.getEmail().equalsIgnoreCase(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "邮箱已存在");
            }
            user.setEmail(request.getEmail());
        }
        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getEnabled() != null) {
            user.setEnabled(request.getEnabled());
        }
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            user.setRoles(loadRoles(request.getRoles()));
        }
        return UserMapper.toResponse(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在");
        }
        userRepository.deleteById(id);
    }

    private Set<Role> loadRoles(Collection<String> roleNames) {
        Collection<String> normalized = (roleNames == null || roleNames.isEmpty())
                ? Set.of(RbacConstants.ROLE_USER)
                : roleNames.stream().map(name -> name.trim().toUpperCase()).collect(Collectors.toSet());
        Set<Role> roles = roleRepository.findByNameIn(normalized);
        if (roles.size() != normalized.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "存在非法角色");
        }
        return roles;
    }
}
