package com.thoroldvix.economatic.stats.goldprice;

import com.thoroldvix.economatic.goldprice.dto.GoldPriceResponse;
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
