package com.thoroldvix.pricepal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients
@ConfigurationPropertiesScan
public class PricePalApplication  {

    public static void main(String[] args) {
        SpringApplication.run(PricePalApplication.class, args);
    }
}