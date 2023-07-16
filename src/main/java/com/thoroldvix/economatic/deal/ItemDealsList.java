package com.thoroldvix.economatic.deal;

import lombok.Builder;

import java.util.List;

@Builder
public record ItemDealsList(
        List<ItemDealResponse> deals
) {

}
