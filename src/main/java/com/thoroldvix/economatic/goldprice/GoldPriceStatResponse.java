package com.thoroldvix.economatic.goldprice;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record GoldPriceStatResponse(
        BigDecimal mean,
        BigDecimal median,
        GoldPriceResponse minimum,
        GoldPriceResponse maximum,
        long count
) {

}