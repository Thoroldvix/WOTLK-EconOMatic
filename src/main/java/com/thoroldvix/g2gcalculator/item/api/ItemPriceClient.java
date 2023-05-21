package com.thoroldvix.g2gcalculator.item.api;

import com.thoroldvix.g2gcalculator.item.dto.ItemInfo;
import com.thoroldvix.g2gcalculator.item.dto.ItemPriceList;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "item", url = "https://api.nexushub.co/wow-classic/v1/items")
public interface ItemPriceClient {

    @GetMapping("/{serverName}/{itemId}")
    ItemInfo getItemById(@PathVariable String serverName, @PathVariable int itemId);

    @GetMapping("/{serverName}/{itemName}")
    ItemInfo getItemByName(@PathVariable String serverName, @PathVariable String itemName);

    @GetMapping("/{serverName}")
    ItemPriceList getAllItemPricesForServer(@PathVariable String serverName);
}