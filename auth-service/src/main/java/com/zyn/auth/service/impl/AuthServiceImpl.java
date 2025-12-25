package com.zyn.auth.service.impl;

import com.zyn.auth.security.JwtTokenProvider;
import com.zyn.auth.service.AuthService;
import com.zyn.auth.service.UserService;
import com.zyn.common.constant.SecurityConstants;
import com.zyn.common.dto.request.LoginRequest;
import com.zyn.common.dto.request.RegisterRequest;
import com.zyn.common.dto.response.AuthResponse;
import com.zyn.common.entity.User;
import com.zyn.common.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * 认证服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;



    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("注册用户: {}", request.getUsername());

        // 创建用户
        User user = userService.createUser(request);

        // 生成JWT令牌
        String token = jwtTokenProvider.generateToken(user);

        return buildAuthResponse(user, token);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("用户登录: {}", request.getUsername());

        try {
            // Spring Security认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 获取用户信息
            User user = userService.findByUsername(request.getUsername());

            // 更新最后登录时间
            userService.updateLastLoginTime(user.getId());

            // 生成JWT令牌
            String token = jwtTokenProvider.generateToken(user);

            return buildAuthResponse(user, token);

        } catch (Exception e) {
            log.error("登录失败", e);
            throw e;
        }
    }

    @Override
    public AuthResponse refreshToken(String token) {
        // 移除 "Bearer " 前缀
        String jwt = token.replace(SecurityConstants.JWT_TOKEN_PREFIX, "");

        // 验证并刷新令牌
        if (jwtTokenProvider.validateToken(jwt)) {
            String username = jwtTokenProvider.getUsernameFromToken(jwt);
            User user = userService.findByUsername(username);
            String newToken = jwtTokenProvider.generateToken(user);

            return buildAuthResponse(user, newToken);
        }

        throw UnauthorizedException.invalidToken();
    }

    @Override
    public void logout(String token) {
        String jwt = token.replace(SecurityConstants.JWT_TOKEN_PREFIX, "");
        if (jwtTokenProvider.validateToken(jwt)) {
             // Logic kept for potential future use (e.g. token blacklist)
        }
        log.info("用户登出");
    }

    private String getClientIp() {
        try {
            org.springframework.web.context.request.ServletRequestAttributes attributes = 
                (org.springframework.web.context.request.ServletRequestAttributes) org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                jakarta.servlet.http.HttpServletRequest request = attributes.getRequest();
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                    return xForwardedFor.split(",")[0].trim();
                }
                return request.getRemoteAddr();
            }
        } catch (Exception e) {
            // ignore
        }
        return "Unknown";
    }

    @Override
    public AuthResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("未认证");
        }

        String username = authentication.getName();
        User user = userService.findByUsername(username);

        return AuthResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toSet()))
                .build();
    }

    /**
     * 构建认证响应
     */
    private AuthResponse buildAuthResponse(User user, String token) {
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(SecurityConstants.JWT_EXPIRATION_MS)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toSet()))
                .build();
    }
}
