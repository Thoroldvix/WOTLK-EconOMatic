package com.example.g2gcalculator.api;

import com.example.g2gcalculator.error.ApiError;
import com.example.g2gcalculator.service.AuctionHouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/wow-classic/v1/ah")
@RequiredArgsConstructor
@Validated
public class ClassicAuctionHouseController {
    public static final int MAX_AH_ID = 476;
    public static final int MIN_AH_ID = 279;
    private final AuctionHouseService classicAuctionHouseService;

    @GetMapping("/{auctionHouseId:^[1-9]\\d*$}")
    public ResponseEntity<?> getAllAuctionHouseItems(@PathVariable int auctionHouseId) {
            if (auctionHouseId > MAX_AH_ID || auctionHouseId < MIN_AH_ID) {
                return new ResponseEntity<>(new ApiError(HttpStatus.NOT_FOUND.value(), "Not found"),
                        HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok(classicAuctionHouseService.getAllItemsByAuctionHouseId(auctionHouseId));
    }
    @GetMapping("/{auctionHouseId:^[1-9]\\d*$}/items/{itemId:^[1-9]\\d*$}")
    public ResponseEntity<?> getAuctionHouseItem(@PathVariable int auctionHouseId, @PathVariable  int itemId) {
        if (auctionHouseId > MAX_AH_ID || auctionHouseId < MIN_AH_ID) {
                return new ResponseEntity<>(new ApiError(HttpStatus.NOT_FOUND.value(), "Not found"),
                        HttpStatus.NOT_FOUND);
            }
        return ResponseEntity.ok(classicAuctionHouseService.getAuctionHouseItem(auctionHouseId, itemId));
    }

}