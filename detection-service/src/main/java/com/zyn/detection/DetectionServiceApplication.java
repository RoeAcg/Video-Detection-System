package com.zyn.detection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.context.annotation.ComponentScan;

/**
 * 检测服务启动类
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@ComponentScan(basePackages = {"com.zyn.detection", "com.zyn.common"})
@EntityScan(basePackages = "com.zyn.common.entity")
@EnableFeignClients(basePackages = "com.zyn.aiclient.feign")
public class DetectionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DetectionServiceApplication.class, args);
    }
}
