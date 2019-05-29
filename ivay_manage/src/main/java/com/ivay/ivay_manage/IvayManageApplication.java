package com.ivay.ivay_manage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(scanBasePackages ="com.ivay.ivay_repository")
@MapperScan("com.ivay.ivay_repository.dao.master")
public class IvayManageApplication {
    public static void main(String[] args) {
        SpringApplication.run(IvayManageApplication.class, args);
    }
}
