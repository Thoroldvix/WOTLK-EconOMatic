package com.thoroldvix.economatic.itemprice.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ItemPriceListResponse(
        List<ItemPriceResponse> prices
) {

}
