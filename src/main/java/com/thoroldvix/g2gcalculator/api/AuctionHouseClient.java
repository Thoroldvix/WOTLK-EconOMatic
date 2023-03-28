package com.thoroldvix.g2gcalculator.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auction-house", url = "https://pricing-api.tradeskillmaster.com")
public interface AuctionHouseClient {
    @GetMapping(value = "/ah/{auctionHouseId}", produces = "application/json")
    String getAllAuctionHouseItems(@PathVariable Integer auctionHouseId);

    @GetMapping(value = "/ah/{auctionHouseId}/item/{itemId}", produces = "application/json")
    String getAuctionHouseItem(@PathVariable Integer auctionHouseId, @PathVariable Integer itemId);
}