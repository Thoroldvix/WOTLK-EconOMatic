package com.thoroldvix.economatic.goldprice.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.thoroldvix.economatic.shared.dto.Filters;
import lombok.Builder;

import java.util.List;

@Builder
public record GoldPricesResponse(
        @JsonUnwrapped
        Filters filters,
        List<GoldPriceResponse> prices
) {
}
