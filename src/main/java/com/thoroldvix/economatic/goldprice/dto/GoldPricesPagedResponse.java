package com.thoroldvix.economatic.goldprice.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;

@Builder
public record GoldPricesPagedResponse(
        int page,
        int pageSize,
        int totalPages,
        long totalElements,
        @JsonUnwrapped
        GoldPricesResponse pricesResponse
) {
}
