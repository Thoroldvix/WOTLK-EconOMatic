package com.example.g2gcalculator.api;

import com.example.g2gcalculator.service.AuctionHouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/wow-classic/v1/ah")
@RequiredArgsConstructor
public class ClassicAuctionHouseController {

    private final AuctionHouseService classicAuctionHouseService;

    @GetMapping("/{auctionHouseId}")
    public ResponseEntity<?> getAllAuctionHouseItems(@PathVariable Integer auctionHouseId) {
        return ResponseEntity.ok(classicAuctionHouseService.getAllItemsByAuctionHouseId(auctionHouseId));
    }
    @GetMapping("/{auctionHouseId}/item/{itemId}")
    public ResponseEntity<?> getAuctionHouseItem(@PathVariable Integer auctionHouseId, @PathVariable Integer itemId) {
        return ResponseEntity.ok(classicAuctionHouseService.getAuctionHouseItem(auctionHouseId, itemId));
    }
}