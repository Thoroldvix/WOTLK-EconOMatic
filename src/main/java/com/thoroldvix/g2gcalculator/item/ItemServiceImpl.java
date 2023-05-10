package com.thoroldvix.g2gcalculator.item;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.thoroldvix.g2gcalculator.common.StringFormatter;
import com.thoroldvix.g2gcalculator.item.dto.AuctionHouseInfo;
import com.thoroldvix.g2gcalculator.item.dto.ItemInfo;
import com.thoroldvix.g2gcalculator.item.price.AuctionHouseService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j

public class ItemServiceImpl implements ItemService {

    private final ItemsClient itemsClient;
    private final ItemRepository itemRepository;
    private final AuctionHouseService auctionHouseServiceImpl;
    private final Map<Integer, Item> boeItemsCache = new HashMap<>();

    private final Cache<String, Set<ItemInfo>> cache;

    @Autowired
    public ItemServiceImpl(ItemsClient itemsClient, ItemRepository itemRepository, AuctionHouseService auctionHouseServiceImpl) {
        this.itemsClient = itemsClient;
        this.itemRepository = itemRepository;
        this.auctionHouseServiceImpl = auctionHouseServiceImpl;
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .maximumSize(96)
                .build();
    }


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
    public Set<ItemInfo> getAllItemsInfo(String server) {
        if (!StringUtils.hasText(server)) {
            throw new IllegalArgumentException("Server name must be valid");
        }
        String formattedServerName = formatServerName(server);
        log.info("Getting all items info for server {}", formattedServerName);
        String cacheKey = "all-items-info-" + server;

        Set<ItemInfo> cachedItems = cache.getIfPresent(cacheKey);
        if (cachedItems != null) {
            log.debug("Returning cached all items info for server {}", formattedServerName);
            return cachedItems;
        }

        List<AuctionHouseInfo> itemPrices = auctionHouseServiceImpl.getAuctionHouseItemsForServer(server);
        Map<Integer, Item> boeItemsCache = getBoeItemsCache(itemPrices);
        log.info("Finished getting all items info for server {}", formattedServerName);
        Set<ItemInfo> itemsInfo = buildFullItemInfo(itemPrices, boeItemsCache);
        cache.put(cacheKey, itemsInfo);
        return itemsInfo;
    }


    private Map<Integer, Item> getBoeItemsCache(List<AuctionHouseInfo> itemPrices) {
        Set<Integer> itemIds = itemPrices.stream()
                .map(AuctionHouseInfo::itemId)
                .filter(itemId -> !boeItemsCache.containsKey(itemId))
                .collect(Collectors.toSet());
        List<Item> items = itemRepository.findAllById(itemIds);
        items.forEach(item -> boeItemsCache.putIfAbsent(item.id, item));
        return boeItemsCache;
    }

    private Set<ItemInfo> buildFullItemInfo(List<AuctionHouseInfo> itemPrices, Map<Integer, Item> cachedItems) {
        return itemPrices.stream()
                .filter(itemInfo -> cachedItems.containsKey(itemInfo.itemId()))
                .filter(itemInfo -> itemInfo.quantity() > 0)
                .map(itemInfo -> createFullItemInfo(itemInfo, cachedItems.get(itemInfo.itemId())))
                .collect(Collectors.toSet());
    }

    private ItemInfo createFullItemInfo(AuctionHouseInfo itemPrice, Item item) {
        return ItemInfo.builder()
                .name(item.name)
                .icon(createItemIcon(item.icon))
                .quality(item.quality)
                .type(item.type)
                .auctionHouseInfo(itemPrice)
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