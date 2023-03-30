package com.thoroldvix.g2gcalculator.items;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemsClient itemsClient;
    @Override
    public ItemStats getItemByName(String serverName, String itemName) {
        return itemsClient.getItemByName(serverName, itemName);
    }

    @Override
    public ItemStats getItemById(String serverName, int itemId) {
        return itemsClient.getItemById(serverName, itemId);
    }
}