package com.example.g2gcalculator.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wow-classic/v1/ah")
public class ClassicAuctionHouseController {

    @GetMapping("/{auctionHouseId}")
    public String getAllAuctionHouseItems(@PathVariable Integer auctionHouseId) {
        return "Auction House";
    }
}