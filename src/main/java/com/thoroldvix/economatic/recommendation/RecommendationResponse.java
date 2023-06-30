package com.thoroldvix.economatic.recommendation;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record RecommendationResponse(
      List<Recommendation> recommendations
) {
    @Builder
    public record Recommendation(
            String serverName,
            BigDecimal totalScore,
            BigDecimal itemPriceScore,
            BigDecimal populationScore,
            BigDecimal goldPriceScore
    ){}
}
