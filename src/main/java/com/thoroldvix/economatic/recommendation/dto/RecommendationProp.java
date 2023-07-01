package com.thoroldvix.economatic.recommendation.dto;

import com.thoroldvix.economatic.recommendation.error.InvalidRecommendationPropertyException;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "economatic.recommendation")
public record RecommendationProp(
        BigDecimal itemPriceDefaultWeight,
        BigDecimal populationDefaultWeight,
        BigDecimal goldPriceDefaultWeight,
        int minAllowedPopulation
) {
    public RecommendationProp  {
        if (itemPriceDefaultWeight.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidRecommendationPropertyException("Item price weight cannot be negative");
        } else if(populationDefaultWeight.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidRecommendationPropertyException("Population weight cannot be negative");
        } else if (goldPriceDefaultWeight.compareTo(BigDecimal.ZERO) < 0) {
             throw new InvalidRecommendationPropertyException("Gold price weight cannot be negative");
        } else if (minAllowedPopulation < 0) {
            throw new InvalidRecommendationPropertyException("Min population cannot be negative");
        }
    }
}
