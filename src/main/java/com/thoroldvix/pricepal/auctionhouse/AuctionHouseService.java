package com.thoroldvix.pricepal.auctionhouse;

import com.thoroldvix.pricepal.item.ItemResponse;
import com.thoroldvix.pricepal.item.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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




    private void verifyServerName(String serverName) {
        Objects.requireNonNull(serverName, "Server name must not be null");
        Pattern serverNamePattern = Pattern.compile("^([^-']+)-(.+)$");
        Matcher matcher = serverNamePattern.matcher(serverName);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Server name must be valid");
        }
    }
}
