package com.thoroldvix.economatic.recommendation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Set;

@Builder
public record RecommendationRequest(
        @Min(value = 0, message = "Item price weight cannot be less than 0")
        BigDecimal itemPriceWeight,
        @Min(value = 0, message = "Population weight cannot be less than 0")
        BigDecimal populationWeight,
        @Min(value = 0, message = "Gold price weight cannot be less than 0")
        BigDecimal goldPriceWeight,
        @Min(value = 0, message = "Minimum allowed population cannot be less than 0")
        int minAllowedPopulation,
        @NotEmpty(message = "Item list cannot be null or empty")
        Set<String> itemList
) {

}
