package com.thoroldvix.economatic.itemprice.dto;

import com.thoroldvix.economatic.itemprice.error.InvalidItemPricePropertyException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@ConfigurationProperties(prefix = "economatic.item-price")
public record ItemPriceProp(
        @DurationUnit(ChronoUnit.HOURS)
        Duration updateRate
) {
         public ItemPriceProp {
                if (updateRate.toHours() < 1) {
                        throw new InvalidItemPricePropertyException("Item price update rate cannot be less than 1");
                }
        }
}
