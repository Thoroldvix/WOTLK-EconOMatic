package com.example.g2gcalculator.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "g2g")
public class G2GConfiguration {
    private final String rootUrl = "https://g2g.com/categories";
    private final String wowClassicGoldCategory = "wow-classic-gold";
    private final String euWowRegionId = "ac3f85c1-7562-437e-b125-e89576b9a38e";
    private final String usWowRegionId = "dfced32f-2f0a-4df5-a218-1e068cfadffa";



    /**
     * Represents the sorting option for a product list.
     * It can be set to either "lowest price" or "recommended price".
     **/
    private String sort;


}