package com.thoroldvix.economatic.item;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.thoroldvix.economatic.common.dto.PaginationInfo;
import lombok.Builder;

import java.util.List;

@Builder
public record ItemPageResponse(
        @JsonUnwrapped
        PaginationInfo paginationInfo,
        List<ItemResponse> items
) {

}
