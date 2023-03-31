package com.thoroldvix.g2gcalculator.item;

public interface ItemService {

    ItemStats getItemByName(String serverName, String itemName);

    ItemStats getItemById(String serverName, int itemId);
}