package com.thoroldvix.g2gcalculator.price;

public interface ItemPriceService {


    ItemPriceResponse getPriceForItem(String serverName, String itemName, int amount, boolean minBuyout);

}