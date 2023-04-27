package com.thoroldvix.g2gcalculator.item.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ItemPriceResponse(
        BigDecimal value,

        String currency
) {

}