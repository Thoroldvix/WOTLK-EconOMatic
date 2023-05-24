package com.thoroldvix.pricepal.item.service;

import com.thoroldvix.pricepal.item.dto.ItemInfo;
import com.thoroldvix.pricepal.item.entity.Item;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface ItemService {

    ItemInfo getItem(String itemName);
    ItemInfo getItem(int itemId);
    Set<ItemInfo> getAllItems(Pageable pageable);
    Set<ItemInfo> getAllItems();
    void saveItem(Item item);
    long getItemCount();

    Set<ItemInfo> findItemsByIds(List<Integer> ids);

    List<ItemInfo> searchItems(String query, Pageable pageable);
}