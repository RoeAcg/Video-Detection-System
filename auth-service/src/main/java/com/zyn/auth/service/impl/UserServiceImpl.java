package com.zyn.auth.service.impl;

import com.zyn.auth.repository.RoleRepository;
import com.zyn.auth.repository.UserRepository;
import com.zyn.auth.service.UserService;
import com.zyn.common.dto.request.RegisterRequest;
import com.zyn.common.entity.Role;
import com.zyn.common.entity.User;
import com.zyn.common.enums.UserRole;
import com.zyn.common.exception.BusinessException;
import com.zyn.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;

/**
 * 用户服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User createUser(RegisterRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw BusinessException.duplicateResource("用户名");
        }

        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(request.getEmail())) {
            throw BusinessException.duplicateResource("邮箱");
        }

        // 创建用户
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .isActive(true)
                .roles(new HashSet<>())
                .build();

        // 分配默认角色 (ROLE_USER)
        Role userRole = roleRepository.findByName(UserRole.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("角色", "ROLE_USER"));
        user.getRoles().add(userRole);

        User savedUser = userRepository.save(user);
        log.info("创建用户成功: {}", savedUser.getUsername());

        return savedUser;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("用户", username));
    }

    @Override
    @Transactional
    public void updateLastLoginTime(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);
        });
    }
}
