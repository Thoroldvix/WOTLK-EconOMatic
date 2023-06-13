package com.thoroldvix.pricepal.item;

import lombok.Builder;

@Builder
public record ItemResponse(
        int id,
        ItemType type,
        ItemQuality quality,
        String iconLink,
        ItemSlot slot,
        String wowheadLink,
        String name,
        String uniqueName
) {
}