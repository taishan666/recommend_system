package com.tarzan.recommend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author tarzan
 */
@EnableScheduling
@SpringBootApplication
public class RecommenderApplication {

    public static void main(String[] args){
        SpringApplication.run(RecommenderApplication.class, args);
    }
}
