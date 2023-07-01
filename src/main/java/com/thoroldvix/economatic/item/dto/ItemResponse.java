package com.thoroldvix.economatic.item.dto;

import com.thoroldvix.economatic.item.model.ItemQuality;
import com.thoroldvix.economatic.item.model.ItemSlot;
import com.thoroldvix.economatic.item.model.ItemType;
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