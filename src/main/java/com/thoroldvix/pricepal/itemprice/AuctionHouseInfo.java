package com.thoroldvix.pricepal.itemprice;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;

import java.util.List;

@Builder
@JsonDeserialize(using = AuctionHouseInfoDeserializer.class)
public record AuctionHouseInfo(
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String server,
        List<ItemPriceResponse> items
) {

}
