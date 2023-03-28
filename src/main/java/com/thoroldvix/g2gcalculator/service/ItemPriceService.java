package com.thoroldvix.g2gcalculator.service;

import com.thoroldvix.g2gcalculator.dto.ItemPriceResponse;
import com.thoroldvix.g2gcalculator.dto.ItemResponse;
import com.thoroldvix.g2gcalculator.dto.PriceResponse;

public interface ItemPriceService {


    ItemPriceResponse getPriceForItem(String realmName, int itemId, int amount, boolean minBuyout);

    ItemPriceResponse calculateItemPrice(ItemResponse item, PriceResponse price, int amount, boolean minBuyout);
}