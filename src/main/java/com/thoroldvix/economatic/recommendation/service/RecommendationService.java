package com.thoroldvix.economatic.recommendation.service;

import com.thoroldvix.economatic.goldprice.dto.GoldPriceRequest;
import com.thoroldvix.economatic.goldprice.dto.GoldPriceResponse;
import com.thoroldvix.economatic.goldprice.service.GoldPriceService;
import com.thoroldvix.economatic.itemprice.dto.ItemPriceRequest;
import com.thoroldvix.economatic.itemprice.dto.ItemPriceResponse;
import com.thoroldvix.economatic.itemprice.service.ItemPriceService;
import com.thoroldvix.economatic.population.dto.PopulationResponse;
import com.thoroldvix.economatic.population.service.PopulationService;
import com.thoroldvix.economatic.recommendation.dto.RecommendationProp;
import com.thoroldvix.economatic.recommendation.dto.RecommendationRequest;
import com.thoroldvix.economatic.recommendation.dto.RecommendationListResponse;
import com.thoroldvix.economatic.recommendation.mapper.RecommendationMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Validated
public class RecommendationService {

    private static final BigDecimal MAX_POPULATION = new BigDecimal("20000");
    private static final BigDecimal MAX_ITEM_PRICE_COPPER = new BigDecimal("1500000");
    private static final BigDecimal MAX_GOLD_PRICE_USD = new BigDecimal("0.0025");
    private final RecommendationProp prop;
    private final PopulationService populationService;

    private final GoldPriceService goldPriceService;

    private final ItemPriceService itemPriceService;

    private final RecommendationMapper recommendationMapper;

    public RecommendationService(PopulationService populationService,
                                 GoldPriceService goldPriceService,
                                 ItemPriceService itemPriceService,
                                 RecommendationMapper recommendationMapper,
                                 RecommendationProp prop) {
        this.populationService = populationService;
        this.goldPriceService = goldPriceService;
        this.itemPriceService = itemPriceService;
        this.recommendationMapper = recommendationMapper;
        this.prop = prop;
    }

    public RecommendationListResponse getRecommendationsForItemList(
            @Valid @NotNull(message = "Recommendation request cannot be null")
            RecommendationRequest request,
            @Min(value = 1, message = "Limit cannot be less that 1")
            int limit,
            boolean isMarketValue) {
        Map<String, BigDecimal> populationScores = getPopulationScores(request.populationWeight());
        Set<String> servers = populationScores.keySet();
        Map<String, BigDecimal> itemPriceScores = getItemPriceScores(request.itemList(), request.itemPriceWeight(), servers, isMarketValue);
        Map<String, BigDecimal> goldPriceScores = getGoldPriceScores(request.goldPriceWeight(), servers);

        return recommendationMapper.toRecommendationResponse(itemPriceScores, populationScores, goldPriceScores, limit);
    }

    private Map<String, BigDecimal> getItemPriceScores(Set<String> itemList, BigDecimal itemPriceWeight, Set<String> servers, boolean isMarketValue) {
        BigDecimal weight = itemPriceWeight == null ? this.prop.itemPriceDefaultWeight() : itemPriceWeight;
        ItemPriceRequest request = buildItemPriceRequest(itemList, servers);

        return itemPriceService.getRecentForItemListAndServers(request, Pageable.unpaged()).prices()
                .stream()
                .collect(Collectors.toMap(ItemPriceResponse::server,
                        itemPrice -> calculateWeightedValue(isMarketValue ? itemPrice.marketValue() : itemPrice.minBuyout(), MAX_ITEM_PRICE_COPPER, weight),
                        BigDecimal::add)
                );
    }

    private static ItemPriceRequest buildItemPriceRequest(Set<String> itemList, Set<String> servers) {
        return ItemPriceRequest.builder()
                .itemList(itemList)
                .serverList(servers)
                .build();
    }

    private Map<String, BigDecimal> getPopulationScores(BigDecimal populationWeight) {
        BigDecimal weight = populationWeight == null ? this.prop.populationDefaultWeight() : populationWeight;
        return populationService.getAllRecent().populations().stream()
                .filter(this::filterLowPopulations)
                .collect(Collectors.toMap(
                        PopulationResponse::server,
                        populationResponse -> calculateWeightedValue(populationResponse.value(), MAX_POPULATION, weight)
                ));
    }


    private Map<String, BigDecimal> getGoldPriceScores(BigDecimal goldPriceWeight, Set<String> servers) {
        BigDecimal weight = goldPriceWeight == null ? this.prop.goldPriceDefaultWeight() : goldPriceWeight;
        GoldPriceRequest request = new GoldPriceRequest(servers);
        return goldPriceService.getRecentForServerList(request).prices().stream()
                .collect(Collectors.toMap(
                        GoldPriceResponse::server,
                        goldPriceResponse ->
                                calculateWeightedValue(goldPriceResponse.price(), MAX_GOLD_PRICE_USD, weight)
                ));
    }

    private boolean filterLowPopulations(PopulationResponse population) {
        return population.value() >= this.prop.minAllowedPopulation();
    }

    private static BigDecimal calculateWeightedValue(Number value, BigDecimal maxValue, BigDecimal weight) {
        BigDecimal normalizedValue = (BigDecimal.valueOf(value.doubleValue())).divide(maxValue, MathContext.DECIMAL64);
        return normalizedValue.multiply(weight);
    }
}
