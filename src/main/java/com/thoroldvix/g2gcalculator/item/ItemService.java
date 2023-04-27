package com.thoroldvix.g2gcalculator.item;

import com.thoroldvix.g2gcalculator.item.dto.ItemInfo;

import java.util.List;

public interface ItemService {

    ItemInfo getItemByName(String serverName, String itemName);

    ItemInfo getItemById(String serverName, int itemId);

    List<ItemInfo> getAllItemsInfo(String serverName);

    void saveItem(Item item);

    long getItemCount();
}