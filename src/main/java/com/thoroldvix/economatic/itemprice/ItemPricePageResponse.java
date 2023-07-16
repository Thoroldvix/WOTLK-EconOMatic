package com.thoroldvix.economatic.itemprice;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.thoroldvix.economatic.common.dto.PaginationInfo;
import lombok.Builder;

import java.util.List;


@Builder
public record ItemPricePageResponse(
        @JsonUnwrapped
        PaginationInfo paginationInfo,
        List<ItemPriceResponse> prices
) {
}
