package com.thoroldvix.economatic.recommendation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import static com.thoroldvix.economatic.util.ValidationUtils.notLessThan;
import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
class BasicRecommendationService implements RecommendationService {

    private final ItemPriceScoreProvider itemPriceScoreProvider;
    private final PopulationScoreProvider populationScoreProvider;
    private final GoldPriceScoreProvider goldPriceScoreProvider;
    private final RecommendationMapper recommendationMapper;

    public RecommendationListResponse getRecommendationsForItemList(@Valid RecommendationRequest request, int limit) {
        requireNonNull(request, "Recommendation request cannot be null");
        notLessThan(limit, 1, "Limit cannot be less than 1");

        Map<String, BigDecimal> populationScores = populationScoreProvider.getPopulationScores(request.populationWeight());
        Set<String> servers = populationScores.keySet();
        Map<String, BigDecimal> itemPriceScores = itemPriceScoreProvider.getItemPriceScores(request, servers);
        Map<String, BigDecimal> goldPriceScores = goldPriceScoreProvider.getGoldPriceScores(request.goldPriceWeight(), servers);

        return recommendationMapper.toRecommendationResponse(itemPriceScores, populationScores, goldPriceScores, limit);
    }
}
