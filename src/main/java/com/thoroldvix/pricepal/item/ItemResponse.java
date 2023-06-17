package com.thoroldvix.pricepal.item;

import lombok.Builder;

@Builder
public record ItemResponse(
        int itemId,
        ItemType type,
        long vendorPrice,
        ItemQuality quality,
        ItemSlot slot,
        String name,
        String uniqueName
) {
}