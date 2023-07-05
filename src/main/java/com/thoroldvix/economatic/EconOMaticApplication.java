package com.thoroldvix.economatic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@EnableRetry
@ConfigurationPropertiesScan
public class EconOMaticApplication {
     public static void main(String[] args) {
        SpringApplication.run(EconOMaticApplication.class, args);
    }
}