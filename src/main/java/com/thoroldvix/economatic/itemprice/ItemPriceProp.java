package com.thoroldvix.economatic.itemprice;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@ConfigurationProperties(prefix = "economatic.item-price")
public record ItemPriceProp(
        @DurationUnit(ChronoUnit.HOURS)
        @Min(1)
        Duration updateRate
) {
}
