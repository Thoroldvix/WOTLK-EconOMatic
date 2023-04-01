package com.thoroldvix.g2gcalculator.item;

import com.thoroldvix.g2gcalculator.price.PriceResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Service
public  class ItemPriceCalculatorImpl implements ItemPriceCalculator {
    private static final BigDecimal ONE_GOLD_IN_COPPER = BigDecimal.valueOf(10000);

    private BigDecimal convertPriceToGold(long priceInCopper) {
        return BigDecimal.valueOf(priceInCopper).divide(ONE_GOLD_IN_COPPER, 6, RoundingMode.CEILING);
    }
    @Override
    public BigDecimal calculatePrice(long targetPrice, PriceResponse priceResponse, int amount) {
        Objects.requireNonNull(priceResponse, "Price cannot be null");
        BigDecimal price = priceResponse.value();
        if (amount < 1 || price.compareTo(BigDecimal.ZERO) < 1 || targetPrice < 1) {
            throw new IllegalArgumentException("Amount must be greater than 0, value must be greater than 0 and itemPrice must be greater than 0");
        }
        BigDecimal priceOfItemInGold = convertPriceToGold(targetPrice);

        return price.multiply(priceOfItemInGold)
                .multiply(BigDecimal.valueOf(amount))
                .setScale(6,  RoundingMode.CEILING);
    }
}