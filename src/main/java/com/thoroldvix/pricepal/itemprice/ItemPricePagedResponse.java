package com.thoroldvix.pricepal.itemprice;

import lombok.Builder;

import java.util.List;

@Builder
public record ItemPricePagedResponse(
        int page,
        int pageSize,
        int totalPages,
        long totalElements,
        List<ItemPriceResponse> prices
) {
}
