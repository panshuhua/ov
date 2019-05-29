package com.ivay.ivay_manage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(scanBasePackages ="com.ivay.ivay_repository", exclude = {SecurityAutoConfiguration.class})
@MapperScan("com.ivay.ivay_repository.dao.master")
public class IvayManageApplication {
    public static void main(String[] args) {
        SpringApplication.run(IvayManageApplication.class, args);
    }
}
