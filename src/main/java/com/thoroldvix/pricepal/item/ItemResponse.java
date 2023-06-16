package com.thoroldvix.pricepal.item;

import lombok.Builder;

@Builder
public record ItemResponse(
        int id,
        ItemType type,
        ItemQuality quality,
        ItemSlot slot,
        String name,
        String uniqueName
) {
}