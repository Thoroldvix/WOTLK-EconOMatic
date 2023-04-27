package com.thoroldvix.g2gcalculator.item.price;

import com.thoroldvix.g2gcalculator.item.dto.ItemPriceResponse;

public interface ItemPriceService {


    ItemPriceResponse getPriceForItem(String serverName, String itemName, int amount, boolean minBuyout);

}