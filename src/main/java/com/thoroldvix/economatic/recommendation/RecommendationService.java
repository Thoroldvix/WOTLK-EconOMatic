package com.thoroldvix.economatic.recommendation;

import com.thoroldvix.economatic.recommendation.dto.RecommendationListResponse;
import com.thoroldvix.economatic.recommendation.dto.RecommendationRequest;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

@Validated
public interface RecommendationService {
    RecommendationListResponse getRecommendationsForItemList(@Valid RecommendationRequest request, int limit, boolean marketValue);
}
