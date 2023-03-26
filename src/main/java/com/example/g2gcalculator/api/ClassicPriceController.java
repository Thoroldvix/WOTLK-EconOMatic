package com.example.g2gcalculator.api;

import com.example.g2gcalculator.dto.PriceResponse;
import com.example.g2gcalculator.error.NotFoundException;
import com.example.g2gcalculator.service.PriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/wow-classic/v1/prices")
@RequiredArgsConstructor
public class ClassicPriceController {

    private final PriceService classicPriceService;

     @GetMapping("/{realmName}")
    public ResponseEntity<?> getPriceForRealm(@PathVariable String realmName) {
        return ResponseEntity.ok(classicPriceService.getPriceForRealm(realmName));
    }

    @GetMapping("/{realmName}/all")
    public ResponseEntity<List<PriceResponse>> getAllPricesForRealm(@PathVariable String realmName,
                                                                    Pageable pageable) {
         return ResponseEntity.ok(classicPriceService.getAllPricesForRealm(realmName, pageable));
    }
    @GetMapping("/{realmName}/items/{itemId}")
    public ResponseEntity<?> getPriceForItem(@PathVariable String realmName,
                                             @PathVariable Integer itemId) {
         if (itemId <= 0) {
             throw new IllegalArgumentException("Bad request");
         }
        return ResponseEntity.ok(classicPriceService.getPriceForItem(realmName, itemId));
    }

}