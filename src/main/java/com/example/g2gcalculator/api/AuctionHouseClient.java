package com.example.g2gcalculator.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "auction-house", url = "https://pricing-api.tradeskillmaster.com")
public interface AuctionHouseClient {
    @RequestMapping(method = RequestMethod.GET, value = "/ah/{auctionHouseId}", produces = "application/json")
    String getAllAuctionHouseItems(@PathVariable Integer auctionHouseId);


}