package com.thoroldvix.economatic.goldprice;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@ConfigurationProperties(prefix = "economatic.gold-price")
public record GoldPriceProp(@DurationUnit(ChronoUnit.MINUTES) Duration updateRate) {

    public GoldPriceProp {
        if (updateRate.toMinutes() < 5) {
            throw new InvalidGoldPricePropertyException("Gold price update rate cannot be less than 5 minutes");
        }
    }
}