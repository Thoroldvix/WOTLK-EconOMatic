package com.thoroldvix.g2gcalculator.item;

import com.thoroldvix.g2gcalculator.item.ItemPriceResponse;

public interface ItemPriceService {


    ItemPriceResponse getPriceForItem(String serverName, String itemName, int amount, boolean minBuyout);

}