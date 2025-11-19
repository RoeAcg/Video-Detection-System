package com.zyn.auth.controller;

import com.zyn.auth.service.AuthService;
import com.zyn.common.dto.request.LoginRequest;
import com.zyn.common.dto.request.RegisterRequest;
import com.zyn.common.dto.response.AuthResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("用户注册请求: {}", request.getUsername());
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("用户登录请求: {}", request.getUsername());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 刷新令牌
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestHeader("Authorization") String token) {
        log.info("刷新令牌请求");
        AuthResponse response = authService.refreshToken(token);
        return ResponseEntity.ok(response);
    }

    /**
     * 登出
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        log.info("用户登出请求");
        authService.logout(token);
        return ResponseEntity.ok("登出成功");
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser() {
        log.info("获取当前用户信息");
        AuthResponse response = authService.getCurrentUser();
        return ResponseEntity.ok(response);
    }
}
