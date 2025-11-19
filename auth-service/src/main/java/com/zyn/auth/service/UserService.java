package com.zyn.auth.service;

import com.zyn.common.dto.request.RegisterRequest;
import com.zyn.common.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 创建用户
     */
    User createUser(RegisterRequest request);

    /**
     * 根据用户名查找用户
     */
    User findByUsername(String username);

    /**
     * 更新最后登录时间
     */
    void updateLastLoginTime(Long userId);
}
