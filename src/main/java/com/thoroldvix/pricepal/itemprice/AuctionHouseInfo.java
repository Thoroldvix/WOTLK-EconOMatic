package com.thoroldvix.pricepal.itemprice;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;

import java.util.List;

@Builder
@JsonDeserialize(using = AuctionHouseInfoDeserializer.class)
public record AuctionHouseInfo(
        String slug,
        List<ItemPriceResponse> items
) {

}
