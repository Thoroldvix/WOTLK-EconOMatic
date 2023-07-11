package com.thoroldvix.economatic.recommendation;

import com.thoroldvix.economatic.goldprice.GoldPriceService;
import com.thoroldvix.economatic.goldprice.dto.GoldPriceRequest;
import com.thoroldvix.economatic.goldprice.dto.GoldPriceResponse;
import com.thoroldvix.economatic.itemprice.ItemPriceService;
import com.thoroldvix.economatic.itemprice.dto.ItemPriceRequest;
import com.thoroldvix.economatic.itemprice.dto.ItemPriceResponse;
import com.thoroldvix.economatic.population.PopulationService;
import com.thoroldvix.economatic.population.dto.PopulationResponse;
import com.thoroldvix.economatic.recommendation.dto.RecommendationListResponse;
import com.thoroldvix.economatic.recommendation.dto.RecommendationProp;
import com.thoroldvix.economatic.recommendation.dto.RecommendationRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.thoroldvix.economatic.shared.ValidationUtils.notLessThan;
import static java.util.Objects.requireNonNull;

@Service
public class BasicRecommendationService implements RecommendationService{

    private static final BigDecimal MAX_POPULATION = new BigDecimal("20000");
    private static final BigDecimal MAX_ITEM_PRICE_COPPER = new BigDecimal("15000000");
    private static final BigDecimal MAX_GOLD_PRICE_USD = new BigDecimal("0.0025");
    private final RecommendationProp prop;
    private final PopulationService populationService;

    private final GoldPriceService goldPriceService;

    private final ItemPriceService itemPriceService;

    private final RecommendationMapper recommendationMapper;

    @Autowired
    public BasicRecommendationService(PopulationService populationService,
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

    private static ItemPriceRequest buildItemPriceRequest(Set<String> itemList, Set<String> servers) {
        return ItemPriceRequest.builder()
                .itemList(itemList)
                .serverList(servers)
                .build();
    }

    private static BigDecimal getWeightOrDefault(BigDecimal weight, BigDecimal defaultWeight) {
        return weight == null ? defaultWeight : weight;
    }

    private static BigDecimal calculateWeightedValue(Number value, BigDecimal maxValue, BigDecimal weight) {
        BigDecimal normalizedValue = (BigDecimal.valueOf(value.doubleValue())).divide(maxValue, MathContext.DECIMAL64);
        return normalizedValue.multiply(weight);
    }

    public RecommendationListResponse getRecommendationsForItemList(@Valid RecommendationRequest request, int limit, boolean marketValue) {
        requireNonNull(request, "Recommendation request cannot be null");
        notLessThan(limit, 1, "Limit cannot be less than 1");

        Map<String, BigDecimal> populationScores = getPopulationScores(request.populationWeight());
        Set<String> servers = populationScores.keySet();
        Map<String, BigDecimal> itemPriceScores = getItemPriceScores(request.itemList(), request.itemPriceWeight(), servers, marketValue);
        Map<String, BigDecimal> goldPriceScores = getGoldPriceScores(request.goldPriceWeight(), servers);

        return recommendationMapper.toRecommendationResponse(itemPriceScores, populationScores, goldPriceScores, limit);
    }

    private Map<String, BigDecimal> getItemPriceScores(Set<String> itemList, BigDecimal itemPriceWeight, Set<String> servers, boolean marketValue) {
        BigDecimal weight = getWeightOrDefault(itemPriceWeight, this.prop.itemPriceDefaultWeight());
        ItemPriceRequest request = buildItemPriceRequest(itemList, servers);

        return itemPriceService.getRecentForItemListAndServers(request, Pageable.unpaged()).prices()
                .stream()
                .collect(Collectors.toMap(ItemPriceResponse::server,
                        itemPrice -> calculateWeightedValue(marketValue ? itemPrice.marketValue() : itemPrice.minBuyout(), MAX_ITEM_PRICE_COPPER, weight),
                        BigDecimal::add)
                );
    }

    private Map<String, BigDecimal> getPopulationScores(BigDecimal populationWeight) {
        BigDecimal weight = getWeightOrDefault(populationWeight, this.prop.populationDefaultWeight());
        return populationService.getAllRecent().populations().stream()
                .filter(this::filterLowPopulations)
                .collect(Collectors.toMap(
                        PopulationResponse::server,
                        populationResponse -> calculateWeightedValue(populationResponse.value(), MAX_POPULATION, weight)
                ));
    }

    private Map<String, BigDecimal> getGoldPriceScores(BigDecimal goldPriceWeight, Set<String> servers) {
        BigDecimal weight = getWeightOrDefault(goldPriceWeight, this.prop.goldPriceDefaultWeight());
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
}
