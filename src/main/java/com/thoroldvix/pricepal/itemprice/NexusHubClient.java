package com.thoroldvix.pricepal.itemprice;

import com.thoroldvix.pricepal.item.ItemResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "item", url = "https://api.nexushub.co/wow-classic/v1/items")
public interface NexusHubClient {


    @GetMapping("/{serverName}/{itemId}")
    ItemResponse getItemPriceById(@PathVariable String serverName, @PathVariable int itemId);

    @GetMapping("/{serverName}/{itemName}")
    ItemResponse getItemPriceByName(@PathVariable String serverName, @PathVariable String itemName);

    @GetMapping("/{serverName}")
    AuctionHouseInfo getAllItemPricesForServer(@PathVariable String serverName);
}