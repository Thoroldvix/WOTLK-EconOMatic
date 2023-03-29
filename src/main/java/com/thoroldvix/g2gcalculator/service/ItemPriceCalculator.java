package com.thoroldvix.g2gcalculator.service;

import com.thoroldvix.g2gcalculator.dto.ItemPriceResponse;
import com.thoroldvix.g2gcalculator.dto.PriceResponse;

import java.math.BigDecimal;

public interface ItemPriceCalculator {
    ItemPriceResponse calculatePrice(long itemPrice, PriceResponse price, int amount);


}