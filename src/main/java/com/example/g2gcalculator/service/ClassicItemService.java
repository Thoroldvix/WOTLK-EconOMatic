package com.example.g2gcalculator.service;

import com.example.g2gcalculator.dto.ItemPriceResponse;
import com.example.g2gcalculator.dto.ItemResponse;
import com.example.g2gcalculator.dto.PriceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class ClassicItemService implements ItemService {

    private static final BigDecimal ONE_GOLD_IN_COPPER = BigDecimal.valueOf(10000);

    @Override
    public ItemPriceResponse calculateItemPriceMinBo(ItemResponse item, PriceResponse price, int amount) {
        if (item == null || price == null) {
            throw new IllegalArgumentException("Item and price cannot be null");
        }
        BigDecimal priceOfItemInGold = convertPriceToGold(item.minBuyout());
        BigDecimal totalPrice = price.value().multiply(priceOfItemInGold).multiply(BigDecimal.valueOf(amount));

        return new ItemPriceResponse(totalPrice);
    }

    @Override
    public ItemPriceResponse calculateItemPriceMVal(ItemResponse item, PriceResponse price, int amount) {
        if (item == null || price == null) {
            throw new IllegalArgumentException("Item and price cannot be null");
        }
        BigDecimal priceOfItemInGold = convertPriceToGold(item.marketValue());
        BigDecimal totalPrice = price.value().multiply(priceOfItemInGold).multiply(BigDecimal.valueOf(amount));

        return new ItemPriceResponse(totalPrice);
    }

    private static BigDecimal convertPriceToGold(long priceInCopper) {
        return BigDecimal.valueOf(priceInCopper).divide(ONE_GOLD_IN_COPPER, RoundingMode.HALF_UP);
    }
}