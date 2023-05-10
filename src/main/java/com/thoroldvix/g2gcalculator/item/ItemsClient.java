package com.thoroldvix.g2gcalculator.item;

import com.thoroldvix.g2gcalculator.item.dto.AuctionHouseInfoList;
import com.thoroldvix.g2gcalculator.item.dto.ItemInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "item", url = "https://api.nexushub.co/wow-classic/v1/items")
public interface ItemsClient {

    @GetMapping("/{serverName}/{itemId}")
    ItemInfo getItemById(@PathVariable String serverName, @PathVariable int itemId);

    @GetMapping("/{serverName}/{itemName}")
    ItemInfo getItemByName(@PathVariable String serverName, @PathVariable String itemName);

    @GetMapping("/{serverName}")
    AuctionHouseInfoList getAllItemPricesForServer(@PathVariable String serverName);
}