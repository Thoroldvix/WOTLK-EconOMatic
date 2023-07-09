package com.thoroldvix.economatic.item.dto;

import com.thoroldvix.economatic.item.ItemQuality;
import com.thoroldvix.economatic.item.ItemSlot;
import com.thoroldvix.economatic.item.ItemType;
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