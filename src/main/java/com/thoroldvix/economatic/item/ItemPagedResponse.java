package com.thoroldvix.economatic.item;

import lombok.Builder;

import java.util.List;

@Builder
public record ItemPagedResponse(
        int page,
        int pageSize,
        int totalPages,
        long totalElements,
        List<ItemResponse> items
) {
}
