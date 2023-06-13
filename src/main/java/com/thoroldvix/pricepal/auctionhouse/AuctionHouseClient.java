package com.thoroldvix.pricepal.auctionhouse;

import com.thoroldvix.pricepal.item.ItemInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "item", url = "https://api.nexushub.co/wow-classic/v1/items")
public interface AuctionHouseClient {

    //todo fix this
    @GetMapping("/{serverName}/{itemId}")
    ItemInfo getItemPriceById(@PathVariable String serverName, @PathVariable int itemId);

    @GetMapping("/{serverName}/{itemName}")
    ItemInfo getItemPriceByName(@PathVariable String serverName, @PathVariable String itemName);

    @GetMapping("/{serverName}")
    AuctionHouseInfo getAllItemPricesForServer(@PathVariable String serverName);
}