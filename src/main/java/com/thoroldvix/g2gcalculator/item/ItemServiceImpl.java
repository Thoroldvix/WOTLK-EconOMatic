package com.thoroldvix.g2gcalculator.item;

import com.thoroldvix.g2gcalculator.common.NotFoundException;
import com.thoroldvix.g2gcalculator.common.StringFormatter;
import com.thoroldvix.g2gcalculator.item.dto.ItemInfo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {


    private final ItemsClient itemsClient;
    private final ItemRepository itemRepository;
    private final Map<Integer, Item> itemCache = new HashMap<>();


    @Override
    public ItemInfo getItemByName(String server, String itemName) {
        if (!StringUtils.hasText(itemName) || !StringUtils.hasText(server))
            throw new IllegalArgumentException("Server name and item name must be valid");

        String formattedItemName = formatItemName(itemName);
        String formattedServerName = formatServerName(server);
        return itemsClient.getItemByName(formattedServerName, formattedItemName);
    }

    @Override
    public ItemInfo getItemById(String server, int itemId) {
        if (itemId < 1 || !StringUtils.hasText(server))
            throw new IllegalArgumentException("Server name and item id must be valid");
        String formattedServerName = formatServerName(server);
        return itemsClient.getItemById(formattedServerName, itemId);
    }

    @Override
    public List<ItemInfo> getAllItemsInfo(String server) {
        if (!StringUtils.hasText(server))
            throw new IllegalArgumentException("Server name must be valid");

        String formattedServerName = formatServerName(server);

        List<ItemInfo> basicItemInfos = itemsClient.getAllItems(formattedServerName).items();
        List<ItemInfo> itemInfos = new ArrayList<>();

        for (ItemInfo basicItemInfo : basicItemInfos) {
            Item item = itemCache.get(basicItemInfo.itemId());
            if (item == null) {
                item = itemRepository.findById(basicItemInfo.itemId()).orElseThrow(() ->
                        new NotFoundException("Item with id " + basicItemInfo.itemId() + " not found"));
                itemCache.put(basicItemInfo.itemId(), item);
            }
            ItemInfo fullItemInfo = createFullItemInfo(item, basicItemInfo);
            itemInfos.add(fullItemInfo);
        }

        return itemInfos;
    }


    private ItemInfo createFullItemInfo(Item item, ItemInfo basicItemInfo) {
        return ItemInfo.builder()
                .itemId(item.id)
                .name(item.name)
                .icon(item.icon)
                .quality(item.quality)
                .type(item.type)
                .marketValue(basicItemInfo.marketValue())
                .minBuyout(basicItemInfo.minBuyout())
                .quantity(basicItemInfo.quantity())
                .numAuctions(basicItemInfo.numAuctions())
                .build();
    }


    @Override
    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    @Override
    public long getItemCount() {
        return itemRepository.count();
    }


    private String formatItemName(String itemName) {
        return StringFormatter.formatString(itemName, String::toLowerCase);
    }

    private String formatServerName(String serverName) {
        return StringFormatter.formatString(serverName, word -> word.replaceAll("'", ""));
    }
}