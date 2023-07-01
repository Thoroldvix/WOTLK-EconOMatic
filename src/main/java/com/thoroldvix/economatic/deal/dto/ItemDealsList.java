package com.thoroldvix.economatic.deal.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ItemDealsList(
        List<ItemDealResponse> deals
) {
}
