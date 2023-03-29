package com.thoroldvix.g2gcalculator.service;

import com.thoroldvix.g2gcalculator.dto.ItemPriceResponse;
import com.thoroldvix.g2gcalculator.dto.ItemResponse;
import com.thoroldvix.g2gcalculator.dto.PriceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Service
@Slf4j
public class ClassicItemPriceCalculator implements ItemPriceCalculator {
    private static final BigDecimal ONE_GOLD_IN_COPPER = BigDecimal.valueOf(10000);

    private BigDecimal convertPriceToGold(long priceInCopper) {
        return BigDecimal.valueOf(priceInCopper).divide(ONE_GOLD_IN_COPPER, RoundingMode.HALF_UP);
    }

    @Override
    public ItemPriceResponse calculatePrice(long itemPrice, BigDecimal price, int amount) {
        Objects.requireNonNull(price, "Price cannot be null");
        if (amount < 1 || price.compareTo(BigDecimal.ZERO) < 1 || itemPrice < 1) {
            throw new IllegalArgumentException("Amount must be greater than 0, price must be greater than 0 and itemPrice must be greater than 0");
        }
        BigDecimal priceOfItemInGold = convertPriceToGold(itemPrice);
        BigDecimal totalPrice = price.multiply(priceOfItemInGold).multiply(BigDecimal.valueOf(amount));

        return new ItemPriceResponse(totalPrice);
    }
}