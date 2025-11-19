package com.zyn.auth.service;

import com.zyn.common.dto.request.LoginRequest;
import com.zyn.common.dto.request.RegisterRequest;
import com.zyn.common.dto.response.AuthResponse;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户注册
     */
    AuthResponse register(RegisterRequest request);

    /**
     * 用户登录
     */
    AuthResponse login(LoginRequest request);

    /**
     * 刷新令牌
     */
    AuthResponse refreshToken(String token);

    /**
     * 登出
     */
    void logout(String token);

    /**
     * 获取当前用户信息
     */
    AuthResponse getCurrentUser();
}
