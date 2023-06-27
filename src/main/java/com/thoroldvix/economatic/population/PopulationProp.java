package com.thoroldvix.economatic.population;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@ConfigurationProperties(prefix = "economatic.population")
public record PopulationProp(
        @DurationUnit(ChronoUnit.DAYS)
        @Min(1)
        Duration updateRate
){
}
