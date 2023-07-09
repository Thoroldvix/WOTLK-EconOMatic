package com.thoroldvix.economatic.population.dto;

import com.thoroldvix.economatic.population.InvalidPopulationPropertyException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

import java.time.Period;
import java.time.temporal.ChronoUnit;

@ConfigurationProperties(prefix = "economatic.population")
public record PopulationProp(
        @DurationUnit(ChronoUnit.DAYS)
        Period updateRate
) {
    public PopulationProp {
        if (updateRate.getDays() < 1) {
            throw new InvalidPopulationPropertyException("Population update rate cannot be less than 1 day");
        }
    }
}
