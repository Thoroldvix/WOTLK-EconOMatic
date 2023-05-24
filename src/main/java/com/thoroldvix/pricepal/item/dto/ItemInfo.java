package com.thoroldvix.pricepal.item.dto;

import com.thoroldvix.pricepal.item.entity.ItemQuality;
import com.thoroldvix.pricepal.item.entity.ItemType;
import lombok.Builder;

@Builder
public record ItemInfo(
        int id,
        ItemType type,
        ItemQuality quality,
        String icon,
        String name,
        String uniqueName
) {
}