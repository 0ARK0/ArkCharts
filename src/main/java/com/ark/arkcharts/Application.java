package com.ark.arkcharts;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication()
@MapperScan("com.ark.arkcharts.mapper")
@ComponentScan(basePackages = {"com.ark.arkcharts.controller", "com.ark.arkcharts.service", "com.ark.arkcharts.serviceImpl", "com.ark.arkcharts.entity", "com.ark.arkcharts.config"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
