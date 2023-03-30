package com.thoroldvix.g2gcalculator.items;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "items", url = "https://api.nexushub.co/wow-classic/v1/items")
public interface ItemsClient {

    @GetMapping("/{serverName}/{itemId}")
    ItemStats getItemById(@PathVariable String serverName, @PathVariable int itemId);

    @GetMapping("/{serverName}/{itemName}")
    ItemStats getItemByName(@PathVariable String serverName, @PathVariable String itemName);
}