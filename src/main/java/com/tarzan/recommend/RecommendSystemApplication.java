package com.tarzan.recommend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author Lenovo
 */
@EnableAsync
@SpringBootApplication
public class RecommendSystemApplication {
    public static void main (String[] args) {
        SpringApplication.run(RecommendSystemApplication.class, args);
    }
}
