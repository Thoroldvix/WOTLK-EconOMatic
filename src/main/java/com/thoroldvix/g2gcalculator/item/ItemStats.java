package com.thoroldvix.g2gcalculator.item;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@JsonDeserialize(using = ItemDeserializer.class)
public record ItemStats(
        String server,
        ItemType type,
        ItemRarity rarity,
        String icon,
        BigDecimal price,
        String currency,
        String name,
        int itemId,
        long minBuyout,
        int quantity,
        LocalDateTime lastUpdated,
        long marketValue

) {
}