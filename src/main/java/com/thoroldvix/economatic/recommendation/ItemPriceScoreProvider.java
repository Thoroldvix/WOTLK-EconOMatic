package com.thoroldvix.economatic.recommendation;

import com.thoroldvix.economatic.common.util.ValidationUtils;
import com.thoroldvix.economatic.itemprice.ItemPriceRequest;
import com.thoroldvix.economatic.itemprice.ItemPriceResponse;
import com.thoroldvix.economatic.itemprice.ItemPriceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Validated
class ItemPriceScoreProvider extends ScoreProvider {

    private static final BigDecimal MAX_ITEM_PRICE_COPPER = new BigDecimal("15000000");
    private final ItemPriceService itemPriceServiceImpl;
    private final BigDecimal itemPriceDefaultWeight;

    @Autowired
    public ItemPriceScoreProvider(ItemPriceService itemPriceServiceImpl, RecommendationProp prop) {
        this.itemPriceServiceImpl = itemPriceServiceImpl;
        this.itemPriceDefaultWeight = prop.itemPriceDefaultWeight();

    }

    public Map<String, BigDecimal> getItemPriceScores(@Valid RecommendationRequest request, Set<String> servers) {
        validateInputs(request, servers);

        BigDecimal itemWeight = getWeightOrDefault(request.itemPriceWeight(), itemPriceDefaultWeight);
        ItemPriceRequest itemPriceRequest = buildItemPriceRequest(request.itemList(), servers);
        List<ItemPriceResponse> recentItemPrices = itemPriceServiceImpl.getRecentForItemListAndServers(itemPriceRequest, Pageable.unpaged()).prices();

        return request.marketValue()
                ? createScoresForMarketValue(recentItemPrices, itemWeight)
                : createScoresForMinBuyout(recentItemPrices, itemWeight);
    }

    private Map<String, BigDecimal> createScoresForMarketValue(List<ItemPriceResponse> recentItemPrices, BigDecimal itemWeight) {

        return recentItemPrices
                .stream()
                .collect(Collectors.toMap(ItemPriceResponse::server,
                        itemPrice -> calculateWeightedValue(itemPrice.marketValue(), itemWeight, MAX_ITEM_PRICE_COPPER),
                        BigDecimal::add)
                );
    }

    private Map<String, BigDecimal> createScoresForMinBuyout(List<ItemPriceResponse> recentItemPrices, BigDecimal itemWeight) {

        return recentItemPrices
                .stream()
                .collect(Collectors.toMap(ItemPriceResponse::server,
                        itemPrice -> calculateWeightedValue(itemPrice.minBuyout(), itemWeight, MAX_ITEM_PRICE_COPPER),
                        BigDecimal::add)
                );
    }

    private void validateInputs(RecommendationRequest request, Set<String> servers) {
        Objects.requireNonNull(request, "Recommendation request cannot be null");
        ValidationUtils.notEmpty(servers, () -> new IllegalArgumentException("Servers cannot be null or empty"));
    }

    private static ItemPriceRequest buildItemPriceRequest(Set<String> itemList, Set<String> servers) {
        return ItemPriceRequest.builder()
                .itemList(itemList)
                .serverList(servers)
                .build();
    }

}


