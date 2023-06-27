package com.thoroldvix.economatic.goldprice;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@ConfigurationProperties(prefix = "economatic.gold-price")
public record GoldPriceProp(
        @DurationUnit(ChronoUnit.MINUTES)
        @Min(1)
        Duration updateRate

) {
}
