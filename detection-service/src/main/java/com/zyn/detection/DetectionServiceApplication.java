package com.zyn.detection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 检测服务启动类
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EntityScan(basePackages = "com.zyn.common.entity")
public class DetectionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DetectionServiceApplication.class, args);
    }
}
