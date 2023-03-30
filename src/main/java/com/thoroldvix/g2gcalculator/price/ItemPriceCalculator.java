package com.thoroldvix.g2gcalculator.price;

import java.math.BigDecimal;

public interface ItemPriceCalculator {
    BigDecimal calculatePrice(long targetPrice, PriceResponse price, int amount);
}