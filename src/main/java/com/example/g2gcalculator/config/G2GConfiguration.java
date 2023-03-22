package com.example.g2gcalculator.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "g2g")
public class G2GConfiguration {
    private final String classicWowUrl = "https://g2g.com/categories/wow-classic-gold?sort=lowest_price";

}