package com.thoroldvix.g2gcalculator.service;

import com.thoroldvix.g2gcalculator.dto.ItemPriceResponse;

import java.math.BigDecimal;

public interface ItemPriceCalculator {
    ItemPriceResponse calculatePrice(long itemPrice, BigDecimal price, int amount);


}