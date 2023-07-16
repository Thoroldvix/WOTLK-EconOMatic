package com.thoroldvix.economatic.recommendation;

import lombok.Builder;

import java.util.List;

@Builder
public record RecommendationListResponse(
        List<RecommendationResponse> recommendations
) {

}
