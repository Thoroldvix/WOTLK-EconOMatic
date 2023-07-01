package com.thoroldvix.economatic.itemprice.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.thoroldvix.economatic.shared.dto.PaginationInfo;
import lombok.Builder;

import java.util.List;


@Builder
public record ItemPricePageResponse(
        @JsonUnwrapped
        PaginationInfo paginationInfo,
        List<ItemPriceResponse> prices
) {
}
