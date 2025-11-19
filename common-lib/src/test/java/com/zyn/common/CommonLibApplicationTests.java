package com.zyn.common;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class CommonLibApplicationTests {
    @Test
    void contextLoads() {
        // 简单的占位测试
        assert true;
    }
}
