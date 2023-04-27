package com.thoroldvix.g2gcalculator.item.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.thoroldvix.g2gcalculator.item.ItemQuality;
import com.thoroldvix.g2gcalculator.item.ItemType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@JsonDeserialize(using = ItemInfoDeserializer.class)
public record ItemInfo(
        String server,
        ItemType type,
        ItemQuality quality,
        String icon,
        BigDecimal price,
        String currency,
        String name,
        int itemId,
        long minBuyout,
        int quantity,
        int numAuctions,
        LocalDateTime lastUpdated,
        long marketValue

) {

}