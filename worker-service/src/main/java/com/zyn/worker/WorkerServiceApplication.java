package com.zyn.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Worker服务启动类
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableKafka
@EnableFeignClients(basePackages = "com.zyn.aiclient.feign")
@EntityScan(basePackages = "com.zyn.common.entity")
public class WorkerServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(WorkerServiceApplication.class, args);
    }
}