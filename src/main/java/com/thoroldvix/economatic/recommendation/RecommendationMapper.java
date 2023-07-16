package com.thoroldvix.economatic.recommendation;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface RecommendationMapper {

    BigDecimal DEFAULT_SCORE = BigDecimal.ZERO;
    int SCALE = 6;

    default RecommendationListResponse toRecommendationResponse(
            Map<String, BigDecimal> itemPriceScores,
            Map<String, BigDecimal> populationScores,
            Map<String, BigDecimal> goldPriceScores,
            int limit) {

        List<RecommendationResponse> recommendations = getRecommendations(itemPriceScores, populationScores, goldPriceScores, limit);
        return RecommendationListResponse.builder()
                .recommendations(recommendations)
                .build();
    }

    private List<RecommendationResponse> getRecommendations(Map<String, BigDecimal> itemPriceScores,
                                                            Map<String, BigDecimal> populationScores,
                                                            Map<String, BigDecimal> goldPriceScores,
                                                            int limit) {
        return goldPriceScores.keySet().stream()
                .map(key ->
                        createRecommendation(
                                key,
                                getOrDefault(itemPriceScores, key),
                                getOrDefault(populationScores, key),
                                getOrDefault(goldPriceScores, key)
                        )
                )
                .sorted(Comparator.comparing(RecommendationResponse::totalScore).reversed())
                .limit(limit)
                .toList();
    }

    private BigDecimal getOrDefault(Map<String, BigDecimal> scores, String key) {
        return Optional.ofNullable(scores.get(key)).orElse(DEFAULT_SCORE).setScale(SCALE, RoundingMode.HALF_UP);
    }

    private RecommendationResponse createRecommendation(String serverName,
                                                        BigDecimal itemPriceScore,
                                                        BigDecimal populationScore,
                                                        BigDecimal goldPriceScore) {
        BigDecimal totalScore = itemPriceScore.add(populationScore).add(goldPriceScore);
        return new RecommendationResponse(serverName, itemPriceScore, populationScore, goldPriceScore, totalScore);
    }
}
