package com.thoroldvix.g2gcalculator.item;

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

        String formattedItemName = formatItemName(itemName);
        String formattedServerName = formatServerName(serverName);
        return itemsClient.getItemByName(formattedServerName, formattedItemName);
    }

    @Override
    public ItemStats getItemById(String serverName, int itemId) {
        if (itemId < 1 || !StringUtils.hasText(serverName))
            throw new IllegalArgumentException("Server name and item id must be valid");
        String formattedServerName = formatServerName(serverName);
        return itemsClient.getItemById(formattedServerName, itemId);
    }

    private String formatItemName(String itemName) {
        return itemName.trim().replaceAll(" ", "-").toLowerCase();
    }

    private String formatServerName(String itemName) {
        return itemName.trim().replaceAll("'", "").toLowerCase();
    }
}