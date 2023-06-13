package com.thoroldvix.pricepal.auctionhouse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.thoroldvix.pricepal.shared.ValidationUtils.hasText;

@RequestMapping("/wow-classic/api/v1/auction-house")
@RestController
@RequiredArgsConstructor
public class AuctionHouseController {

    private final AuctionHouseService auctionHouseService;
    @GetMapping("/{serverName}")
    public ResponseEntity<?> getAuctionHouseInfo(@PathVariable String serverName, @RequestParam(required = false) boolean detailed) {
        if (!hasText(serverName)) {
            return ResponseEntity.badRequest().build();
        }
        if (detailed) {
            return ResponseEntity.ok(auctionHouseService.getFullAuctionHouseInfo(serverName));
        }
        return ResponseEntity.ok(auctionHouseService.getAuctionHouseInfo(serverName));
    }
}
