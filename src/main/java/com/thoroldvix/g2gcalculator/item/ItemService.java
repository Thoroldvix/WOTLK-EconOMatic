package com.thoroldvix.g2gcalculator.item;

import com.thoroldvix.g2gcalculator.item.dto.ItemInfo;

import java.util.Set;

public interface ItemService {

    ItemInfo getItemByName(String serverName, String itemName);

    ItemInfo getItemById(String serverName, int itemId);

    Set<ItemInfo> getAllItemsInfoForServer(String serverName);


    Set<ItemInfo> getAllItemsInfo();
    void saveItem(Item item);

    long getItemCount();
}