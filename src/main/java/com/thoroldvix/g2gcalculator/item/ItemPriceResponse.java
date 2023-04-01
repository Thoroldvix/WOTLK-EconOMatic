package com.thoroldvix.g2gcalculator.item;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ItemPriceResponse(
        BigDecimal value,

        String currency

) {

}