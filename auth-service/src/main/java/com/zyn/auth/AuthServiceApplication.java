package com.zyn.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 认证服务启动类
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableAspectJAutoProxy
@EntityScan(basePackages = "com.zyn.common.entity")
@ComponentScan(basePackages = {"com.zyn.auth", "com.zyn.common"})
public class AuthServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
