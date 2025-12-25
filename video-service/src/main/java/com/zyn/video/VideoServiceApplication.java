package com.zyn.video;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * 视频服务启动类
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableKafka
@EnableAspectJAutoProxy
@EntityScan(basePackages = "com.zyn.common.entity")
@ComponentScan(basePackages = {"com.zyn.video", "com.zyn.common"})
public class VideoServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(VideoServiceApplication.class, args);
    }
}
