package com.coder.enhance;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author jeffy
 * @date 2019/1/25
 **/
@SpringBootApplication
@EnableMybatisPenguin
@MapperScan(basePackages = "com.coder.enhance.mapper")
public class ApplicationBoot implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationBoot.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.err.println("start successfully");
    }
}
