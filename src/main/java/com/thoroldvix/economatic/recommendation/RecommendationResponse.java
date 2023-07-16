package com.thoroldvix.economatic.recommendation;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
record RecommendationResponse(
        String serverName,
        BigDecimal totalScore,
        BigDecimal itemPriceScore,
        BigDecimal populationScore,
        BigDecimal goldPriceScore
) {

}
