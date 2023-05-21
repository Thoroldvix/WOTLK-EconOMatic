package com.thoroldvix.g2gcalculator.item.dto;

import com.thoroldvix.g2gcalculator.item.ItemQuality;
import com.thoroldvix.g2gcalculator.item.ItemType;
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