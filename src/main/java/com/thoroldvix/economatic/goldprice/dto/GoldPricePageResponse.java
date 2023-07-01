package com.thoroldvix.economatic.goldprice.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.thoroldvix.economatic.shared.dto.PaginationInfo;
import lombok.Builder;

import java.util.List;

@Builder
public record GoldPricePageResponse(
        @JsonUnwrapped
        PaginationInfo paginationInfo,
        List<GoldPriceResponse> prices
) {
}
