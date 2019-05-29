package com.ivay.ivay_manage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 同时注入多个模块的bean时，不同bean的包名必须相同
@SpringBootApplication(scanBasePackages = "com.ivay")
public class IvayManageApplication {
    public static void main(String[] args) {
        SpringApplication.run(IvayManageApplication.class, args);
    }
}
