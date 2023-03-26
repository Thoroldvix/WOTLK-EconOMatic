package com.example.g2gcalculator.config;

import com.example.g2gcalculator.model.Currency;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;


@ConfigurationProperties(prefix = "g2g")
public record G2GProperties(
        /**
         * The interval between updates of the price data, in minutes.
         * Default value is 60 (1 hour).
         */
        @DurationUnit(ChronoUnit.MINUTES)
        Duration scrapingInterval,
        /**
         * Flag indicating whether to force an update of the price data, effectively ignoring update interval.
         * Default value is false.
         */
        boolean forceUpdate,
        /**
         * The currency used for price calculations.
         * Default value is EUR (Euro).
         */
        Currency currency,

        String apiKey,

        TsmProperties tsm


) {
    public record TsmProperties(
            String apiKey
    ) {
        public TsmProperties {
            if (apiKey == null || apiKey.isBlank()) {
                throw new IllegalArgumentException("TSM API key must not be null or blank");
            }
        }
    }

    public G2GProperties {
       scrapingInterval =  scrapingInterval == null ? Duration.ofMinutes(60) : scrapingInterval;
       currency = currency == null ? Currency.EUR : currency;


        if (scrapingInterval.isNegative() || scrapingInterval.isZero()) {
            throw new IllegalArgumentException("Scraping interval must be a positive value");
        }
    }
}