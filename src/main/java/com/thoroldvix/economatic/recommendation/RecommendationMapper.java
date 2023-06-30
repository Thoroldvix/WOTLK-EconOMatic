package com.thoroldvix.economatic.recommendation;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RecommendationMapper {

    BigDecimal DEFAULT_SCORE = BigDecimal.ZERO;


    default RecommendationResponse toRecommendationResponse(
            @NotEmpty(message = "Item price scores cannot be null or empty")
            Map<String, BigDecimal> itemPriceScores,
            @NotEmpty(message = "Population scores cannot be null or empty")
            Map<String, BigDecimal> populationScores,
            @NotEmpty(message = "Gold price scores cannot be null or empty")
            Map<String, BigDecimal> goldPriceScores,
            @Min(value = 1, message = "Limit cannot be less than 1")
            int limit) {

        List<RecommendationResponse.Recommendation> recommendations = getRecommendations(itemPriceScores, populationScores, goldPriceScores, limit);
        return RecommendationResponse.builder()
                .recommendations(recommendations)
                .build();
    }

    private List<RecommendationResponse.Recommendation> getRecommendations(Map<String, BigDecimal> itemPriceScores,
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
                .sorted(Comparator.comparing(RecommendationResponse.Recommendation::totalScore)
                        .reversed())
                .limit(limit)
                .toList();
    }

    private BigDecimal getOrDefault(Map<String, BigDecimal> scores, String key) {
        return Optional.ofNullable(scores.get(key)).orElse(DEFAULT_SCORE).setScale(6, RoundingMode.HALF_UP);
    }

    private RecommendationResponse.Recommendation createRecommendation(String serverName,
                                                                       BigDecimal itemPriceScore,
                                                                       BigDecimal populationScore,
                                                                       BigDecimal goldPriceScore) {

        BigDecimal totalScore = itemPriceScore.add(populationScore).add(goldPriceScore);
        return RecommendationResponse.Recommendation.builder()
                .totalScore(totalScore)
                .serverName(serverName)
                .itemPriceScore(itemPriceScore)
                .goldPriceScore(goldPriceScore)
                .populationScore(populationScore)
                .build();
    }
}
