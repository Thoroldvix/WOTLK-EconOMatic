package com.thoroldvix.economatic.goldprice;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.thoroldvix.economatic.shared.Filters;
import lombok.Builder;

import java.util.List;

@Builder
public record GoldPricesResponse(
        @JsonUnwrapped
        Filters filters,
        List<GoldPriceResponse> prices
) {
}
