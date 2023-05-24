package com.thoroldvix.pricepal.item.api;

import com.thoroldvix.pricepal.item.service.AuctionHouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/wow-classic/api/v1/items/prices")
@RestController
@RequiredArgsConstructor
public class ItemPriceController {

    private final AuctionHouseService auctionHouseServiceImpl;
    @GetMapping("/{serverName}")
    public ResponseEntity<?> getAuctionHouseInfo(@PathVariable String serverName, @RequestParam(required = false) boolean detailed) {
        if (!StringUtils.hasText(serverName)) {
            return ResponseEntity.badRequest().build();
        }
        if (detailed) {
            return ResponseEntity.ok(auctionHouseServiceImpl.getFullAuctionHouseInfo(serverName));
        }
        return ResponseEntity.ok(auctionHouseServiceImpl.getAuctionHouseInfo(serverName));
    }
}
