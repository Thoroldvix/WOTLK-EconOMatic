package com.thoroldvix.g2gcalculator.item.service;

import com.thoroldvix.g2gcalculator.item.dto.ItemInfo;
import com.thoroldvix.g2gcalculator.item.entity.Item;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface ItemService {

    ItemInfo getItemByName(String itemName);
    ItemInfo getItemById(int itemId);
    Set<ItemInfo> getAllItems(Pageable pageable);
    void saveItem(Item item);
    long getItemCount();

    List<ItemInfo> findItemsByIds(List<Integer> ids);

    List<ItemInfo> searchItems(String query, Pageable pageable);
}