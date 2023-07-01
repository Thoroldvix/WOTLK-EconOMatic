package com.thoroldvix.economatic.recommendation.dto;

import lombok.Builder;

import java.math.BigDecimal;
@Builder
public record RecommendationResponse(

            String serverName,
            BigDecimal totalScore,
            BigDecimal itemPriceScore,
            BigDecimal populationScore,
            BigDecimal goldPriceScore
){}
