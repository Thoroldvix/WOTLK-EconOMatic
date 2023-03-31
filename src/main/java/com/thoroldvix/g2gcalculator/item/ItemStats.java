package com.thoroldvix.g2gcalculator.item;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
@JsonDeserialize(using = ItemDeserializer.class)
public record ItemStats(
        String server,
        BigDecimal price,
        String currency,
        String name,
        int itemId,
        long minBuyout,
        int quantity,
        long marketValue

) {
}