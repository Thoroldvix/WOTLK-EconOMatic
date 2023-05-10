package com.thoroldvix.g2gcalculator.item.price;

import com.thoroldvix.g2gcalculator.price.PriceResponse;

import java.math.BigDecimal;

public interface RMItemPriceCalculator {
    BigDecimal calculatePrice(long targetPrice, PriceResponse price, int amount);
}