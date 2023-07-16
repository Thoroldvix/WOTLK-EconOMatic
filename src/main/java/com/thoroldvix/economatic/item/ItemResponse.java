package com.thoroldvix.economatic.item;

import lombok.Builder;

@Builder
public record ItemResponse(
        int id,
        ItemType type,
        long vendorPrice,
        ItemQuality quality,
        ItemSlot slot,
        String name,
        String uniqueName
) {

}