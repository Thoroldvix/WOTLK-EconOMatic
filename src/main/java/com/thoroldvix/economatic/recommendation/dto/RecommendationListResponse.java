package com.thoroldvix.economatic.recommendation.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record RecommendationListResponse(
      List<RecommendationResponse> recommendations
) {

}
