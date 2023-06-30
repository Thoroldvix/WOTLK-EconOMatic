package com.thoroldvix.economatic.recommendation;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.Set;

@Builder
@Validated
public record RecommendationRequest(
        @Min(0)
        BigDecimal itemPriceWeight,
        @Min(0)
        BigDecimal populationWeight,
        @Min(0)
        BigDecimal goldPriceWeight,
        @Min(0)
        int minAllowedPopulation,
        @NotEmpty(message = "Item list cannot be null or empty")
        Set<String> itemList
) {

}
