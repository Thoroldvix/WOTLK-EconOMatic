package com.example.g2gcalculator.service;

import com.example.g2gcalculator.dto.ItemPriceResponse;
import com.example.g2gcalculator.dto.ItemResponse;
import com.example.g2gcalculator.dto.PriceResponse;

public interface ItemService {

    ItemPriceResponse calculateItemPriceMinBo(ItemResponse item, PriceResponse price, int amount);

    ItemPriceResponse calculateItemPriceMVal(ItemResponse item, PriceResponse price, int amount);
}