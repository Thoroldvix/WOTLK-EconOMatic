package com.thoroldvix.pricepal.itemprice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "item", url = "https://api.nexushub.co/wow-classic/v1/items")
public interface NexusHubClient {



    @GetMapping("/{serverName}")
    AuctionHouseInfo fetchAllItemPricesForServer(@PathVariable String serverName);
}