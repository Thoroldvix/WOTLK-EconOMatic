package com.thoroldvix.g2gcalculator.item;

public interface ItemPriceService {


    ItemPriceResponse getPriceForItem(String serverName, String itemName, int amount, boolean minBuyout);

}