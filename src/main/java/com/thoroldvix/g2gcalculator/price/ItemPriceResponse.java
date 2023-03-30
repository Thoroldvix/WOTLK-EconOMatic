package com.thoroldvix.g2gcalculator.price;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ItemPriceResponse(
        BigDecimal price,

        String currency

) {

}