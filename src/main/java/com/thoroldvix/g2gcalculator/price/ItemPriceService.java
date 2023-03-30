package com.thoroldvix.g2gcalculator.price;

public interface ItemPriceService {


    ItemPriceResponse getPriceForItem(String serverName, int itemId, int amount, boolean minBuyout);
    ItemPriceResponse getPriceForItem(String serverName, String itemName, int amount, boolean minBuyout);

}