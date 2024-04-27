package com.lukian.userapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.lukian.userapi", "com.lukian.userapi.mapper"})
public class UserapiApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserapiApplication.class, args);
    }

}
