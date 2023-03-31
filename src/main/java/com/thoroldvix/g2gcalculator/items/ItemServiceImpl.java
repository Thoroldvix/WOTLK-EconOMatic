package com.thoroldvix.g2gcalculator.items;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemsClient itemsClient;
    @Override
    public ItemStats getItemByName(String serverName, String itemName) {
        if (!StringUtils.hasText(itemName) || !StringUtils.hasText(serverName))
            throw new IllegalArgumentException("Server name and item name must be valid");
        return itemsClient.getItemByName(serverName, itemName);
    }

    @Override
    public ItemStats getItemById(String serverName, int itemId) {
         if (itemId < 1 || !StringUtils.hasText(serverName))
            throw new IllegalArgumentException("Server name and item id must be valid");
        return itemsClient.getItemById(serverName, itemId);
    }
}