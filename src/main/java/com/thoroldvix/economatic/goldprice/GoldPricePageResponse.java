package com.thoroldvix.economatic.goldprice;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.thoroldvix.economatic.shared.PaginationInfo;
import lombok.Builder;

import java.util.List;

@Builder
public record GoldPricePageResponse(
        @JsonUnwrapped
        PaginationInfo paginationInfo,
        List<GoldPriceResponse> prices
) {
}
