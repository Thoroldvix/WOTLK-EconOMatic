package com.thoroldvix.g2gcalculator.item.dto;

import com.thoroldvix.g2gcalculator.item.entity.ItemQuality;
import com.thoroldvix.g2gcalculator.item.entity.ItemType;
import lombok.Builder;

@Builder
public record ItemInfo(
        int id,
        ItemType type,
        ItemQuality quality,
        String icon,
        String name
) {
    public String getFormattedItemName() {
        return name.replaceAll("\\s+|(:\\s+)", "-").toLowerCase();
    }
}