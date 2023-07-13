package com.thoroldvix.economatic.recommendation;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

@Validated
public interface RecommendationService {
    RecommendationListResponse getRecommendationsForItemList(@Valid RecommendationRequest request, int limit, boolean marketValue);
}
