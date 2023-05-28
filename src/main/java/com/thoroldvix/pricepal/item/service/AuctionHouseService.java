package com.thoroldvix.pricepal.item.service;

import com.thoroldvix.pricepal.item.api.AuctionHouseClient;
import com.thoroldvix.pricepal.item.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuctionHouseService {


    private final AuctionHouseClient auctionHouseClient;
    private final ItemService itemServiceImpl;

    public AuctionHouseInfo getAuctionHouseInfo(String serverName) {
        verifyServerName(serverName);
        return auctionHouseClient.getAllItemPricesForServer(serverName);
    }

    public FullAuctionHouseInfo getFullAuctionHouseInfo(String serverName) {
        verifyServerName(serverName);
        AuctionHouseInfo auctionHouseInfo = auctionHouseClient.getAllItemPricesForServer(serverName);
        String slug = auctionHouseInfo.slug();

        List<Integer> itemIds = auctionHouseInfo.items().stream()
                .map(ItemPrice::id)
                .toList();

        Map<Integer, ItemInfo> itemInfoMap = itemServiceImpl.findItemsByIds(itemIds).stream()
                .collect(Collectors.toMap(ItemInfo::id, Function.identity()));

        List<ItemPrice> validItemPrices = auctionHouseInfo.items().stream()
                .filter(itemPrice -> itemPrice.quantity() > 0 && itemPrice.minBuyout() > 0)
                .filter(itemPrice -> itemInfoMap.containsKey(itemPrice.id()))
                .toList();

        List<FullItemInfo> fullItemInfos = validItemPrices.stream()
                .map(itemPrice -> new FullItemInfo(itemInfoMap.get(itemPrice.id()), itemPrice))
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
