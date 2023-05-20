package com.thoroldvix.g2gcalculator.price.g2g;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;


@ConfigurationProperties(prefix = "g2g")
public record G2GProp(
        @DurationUnit(ChronoUnit.MINUTES)
        Duration priceUpdateInterval,

        boolean forceUpdate,

        String currency
) {


    public G2GProp {
       priceUpdateInterval = priceUpdateInterval == null ? Duration.ofMinutes(60) : priceUpdateInterval;
       if (priceUpdateInterval.isNegative() || priceUpdateInterval.isZero()) {
            throw new IllegalArgumentException("Scraping interval must be a positive value");
        }
       currency = currency == null || currency.isBlank() ? Currency.USD.name() : currency;
    }
}