package com.thoroldvix.economatic.recommendation;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "economatic.recommendation")
record RecommendationProp(
        BigDecimal itemPriceDefaultWeight,
        BigDecimal populationDefaultWeight,
        BigDecimal goldPriceDefaultWeight,
        int minAllowedPopulation
) {

    public RecommendationProp {
        validate(itemPriceDefaultWeight, populationDefaultWeight, goldPriceDefaultWeight, minAllowedPopulation);
    }

    private static void validate(BigDecimal itemPriceDefaultWeight,
                                 BigDecimal populationDefaultWeight,
                                 BigDecimal goldPriceDefaultWeight,
                                 int minAllowedPopulation) {

        if (itemPriceDefaultWeight.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidRecommendationPropertyException("Item price weight cannot be negative");
        }
        if (populationDefaultWeight.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidRecommendationPropertyException("Population weight cannot be negative");
        }
        if (goldPriceDefaultWeight.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidRecommendationPropertyException("Gold price weight cannot be negative");
        }
        if (minAllowedPopulation < 0) {
            throw new InvalidRecommendationPropertyException("Min population cannot be negative");
        }
    }
}
