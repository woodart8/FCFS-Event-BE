package com.woodart8.fcfs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FcfsApplication {

    public static void main(String[] args) {
        SpringApplication.run(FcfsApplication.class, args);
    }

}
