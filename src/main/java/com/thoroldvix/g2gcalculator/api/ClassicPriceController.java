package com.thoroldvix.g2gcalculator.api;

import com.thoroldvix.g2gcalculator.dto.ItemPriceResponse;
import com.thoroldvix.g2gcalculator.dto.PriceResponse;
import com.thoroldvix.g2gcalculator.service.ItemPriceService;
import com.thoroldvix.g2gcalculator.service.PriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wow-classic/v1/prices")
@RequiredArgsConstructor
public class ClassicPriceController {

    private final PriceService classicPriceService;
    private final ItemPriceService classicItemPriceService;

     @GetMapping("/{realmName:\\w+-\\w+}")
    public ResponseEntity<PriceResponse> getPriceForRealm(@PathVariable  String realmName) {
        return ResponseEntity.ok(classicPriceService.getPriceForRealmName(realmName));
    }

    @GetMapping("/{realmName:\\w+-\\w+}/all")
    public ResponseEntity<List<PriceResponse>> getAllPricesForRealm(@PathVariable String realmName,
                                                                    Pageable pageable) {
         return ResponseEntity.ok(classicPriceService.getAllPricesForRealm(realmName, pageable));
    }
    @GetMapping("/{realmName:\\w+-\\w+}/items/{itemId:^[1-9]\\d*$}")
    public ResponseEntity<ItemPriceResponse> getPriceForItem(@PathVariable String realmName,
                                                             @PathVariable Integer itemId,
                                                             @RequestParam(required = false, defaultValue = "1") Integer amount,
                                                             @RequestParam(required = false, defaultValue = "false") Boolean minBuyout) {
         return ResponseEntity.ok(classicItemPriceService.getPriceForItem(realmName, itemId, amount, minBuyout));
    }
}