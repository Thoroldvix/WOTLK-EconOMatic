package com.example.g2gcalculator.config;

import com.example.g2gcalculator.model.Currency;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Data
@Component
@NoArgsConstructor
@ConfigurationProperties(prefix = "g2g")
public class G2GProperties {

        /**
         * The interval between updates of the price data, in seconds.
         * Default value is 3600 (1 hour).
         */
        @DurationUnit(ChronoUnit.MINUTES)
        private Duration scrapingInterval = Duration.ofMinutes(60);
        /**
         * Flag indicating whether to force an update of the price data, effectively ignoring update interval.
         * Default value is false.
         */
        private boolean forceUpdate = false;
        /**
         * The currency used for price calculations.
         * Default value is EUR (Euro).
         */
        private Currency currency = Currency.EUR;
}