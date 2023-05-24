package com.thoroldvix.pricepal.item.service;

import com.thoroldvix.pricepal.item.api.AuctionHouseClient;
import com.thoroldvix.pricepal.item.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuctionHouseServiceImpl implements AuctionHouseService {


    private final AuctionHouseClient auctionHouseClient;
    private final ItemService itemServiceImpl;

    @Override
    public AuctionHouseInfo getAuctionHouseInfo(String serverName) {
        verifyServerName(serverName);
        return auctionHouseClient.getAllItemPricesForServer(serverName);
    }

    @Override
    public FullAuctionHouseInfo getFullAuctionHouseInfo(String serverName) {
        verifyServerName(serverName);
        AuctionHouseInfo auctionHouseInfo = auctionHouseClient.getAllItemPricesForServer(serverName);
        String slug = auctionHouseInfo.slug();

        List<Integer> itemIds = auctionHouseInfo.items().stream()
                .map(ItemPrice::id)
                .toList();
        Map<Integer, ItemInfo> itemInfos = itemServiceImpl.findItemsByIds(itemIds).stream()
                .map(itemInfo -> new AbstractMap.SimpleEntry<>(itemInfo.id(), itemInfo))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        List<ItemPrice> itemPrices = auctionHouseInfo.items();

        List<FullItemInfo> fullItemInfos = itemPrices.stream()
                .filter(itemPrice -> itemPrice.quantity() > 0)
                .filter(itemPrice -> itemPrice.minBuyout() > 0)
                .filter(itemPrice -> itemInfos.containsKey(itemPrice.id()))
                .map(itemPrice -> new FullItemInfo(itemInfos.get(itemPrice.id()), itemPrice))
                .toList();


        return new FullAuctionHouseInfo(slug, fullItemInfos);
    }


    private void verifyServerName(String serverName) {
        Objects.requireNonNull(serverName, "Server name must not be null");
        Pattern serverNamePattern = Pattern.compile("^([^-']+)-(.+)$");
        Matcher matcher = serverNamePattern.matcher(serverName);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Server name must be valid");
        }
    }
}
