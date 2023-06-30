package com.thoroldvix.economatic.itemprice;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.thoroldvix.economatic.shared.Filters;
import lombok.Builder;

import java.util.List;

@Builder
public record AuctionHouseInfo(
        @JsonUnwrapped
        AuctionHouseFilters filters,
        List<ItemPriceResponse> prices
) {
    @Builder
    public record AuctionHouseFilters(
            @JsonUnwrapped
            Filters filters,
            Integer itemId,
            String itemName
    ) {}
}
