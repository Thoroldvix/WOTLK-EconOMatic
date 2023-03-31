package com.thoroldvix.g2gcalculator.price;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wow-classic/v1/prices")
@RequiredArgsConstructor
public class PriceController {

    private final PriceService classicPriceService;
    private final ItemPriceService classicItemPriceService;

    @GetMapping("/{serverName}")
    public ResponseEntity<PriceResponse> getPriceForRealm(@PathVariable String serverName) {
        return ResponseEntity.ok(classicPriceService.getPriceForServerName(serverName));
    }

    @GetMapping("/{serverName}/all")
    public ResponseEntity<List<PriceResponse>> getAllPricesForRealm(@PathVariable String serverName,
                                                                    Pageable pageable) {
        return ResponseEntity.ok(classicPriceService.getAllPricesForServer(serverName, pageable));
    }

    @GetMapping("/{serverName}/{itemName}")
    public ResponseEntity<ItemPriceResponse> getPriceForItem(@PathVariable String serverName,
                                                             @PathVariable String itemName,
                                                             @RequestParam(required = false, defaultValue = "1") Integer amount,
                                                             @RequestParam(required = false, defaultValue = "false") Boolean minBuyout) {

        return ResponseEntity.ok(classicItemPriceService.getPriceForItem(serverName, itemName, amount, minBuyout));
    }
}