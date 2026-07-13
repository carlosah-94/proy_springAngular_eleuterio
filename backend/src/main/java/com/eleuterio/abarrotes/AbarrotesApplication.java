package com.eleuterio.abarrotes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AbarrotesApplication {

    public static void main(String[] args) {
        SpringApplication.run(AbarrotesApplication.class, args);
    }
}
