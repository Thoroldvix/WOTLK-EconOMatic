package com.thoroldvix.g2gcalculator.item;

import com.thoroldvix.g2gcalculator.common.StringFormatter;
import com.thoroldvix.g2gcalculator.item.dto.ItemInfo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {


    private final ItemsClient itemsClient;
    private final ItemRepository itemRepository;
    private final ItemMapper mapper;

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
        validateServerName(server);
        String formattedServerName = formatServerName(server);
        List<ItemInfo> basicItemInfos = fetchBasicItemInfo(formattedServerName);
        Map<Integer, Item> cachedItems = getCachedItems(basicItemInfos);
        return buildFullItemInfo(basicItemInfos, cachedItems);
    }

    private void validateServerName(String server) {
        if (!StringUtils.hasText(server)) {
            throw new IllegalArgumentException("Server name must be valid");
        }
    }

    private List<ItemInfo> fetchBasicItemInfo(String server) {
        return itemsClient.getAllItems(server).items();
    }

    private Map<Integer, Item> getCachedItems(List<ItemInfo> itemInfos) {
        Set<Integer> itemIds = itemInfos.stream()
                .map(ItemInfo::itemId)
                .filter(itemId -> !itemCache.containsKey(itemId))
                .collect(Collectors.toSet());
        List<Item> items = itemRepository.findAllById(itemIds);
        items.forEach(item -> itemCache.putIfAbsent(item.id, item));
        return itemCache;
    }

    private List<ItemInfo> buildFullItemInfo(List<ItemInfo> basicItemInfos, Map<Integer, Item> cachedItems) {
        return basicItemInfos.stream()
                .filter(itemInfo -> cachedItems.containsKey(itemInfo.itemId()))
                .filter(itemInfo -> itemInfo.quantity() > 0)
                .map(itemInfo -> createFullItemInfo(itemInfo, cachedItems.get(itemInfo.itemId())))
                .collect(Collectors.toList());
    }

    


    private ItemInfo createFullItemInfo(ItemInfo basicItemInfo, Item item) {

        return ItemInfo.builder()
                .itemId(basicItemInfo.itemId())
                .name(item.name)
                .icon(createItemIcon(item.icon))
                .quality(item.quality)
                .type(item.type)
                .marketValue(basicItemInfo.marketValue())
                .minBuyout(basicItemInfo.minBuyout())
                .quantity(basicItemInfo.quantity())
                .numAuctions(basicItemInfo.numAuctions())
                .build();

    }

    private String createItemIcon(String icon) {
        String wowheadImgLink = "https://wow.zamimg.com/images/wow/icons/large/%s.jpg";
        return String.format(wowheadImgLink, icon);
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