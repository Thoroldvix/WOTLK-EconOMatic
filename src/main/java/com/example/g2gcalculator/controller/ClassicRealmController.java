package com.example.g2gcalculator.controller;

import com.example.g2gcalculator.model.Faction;
import com.example.g2gcalculator.service.PriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wowc/realms")
@RequiredArgsConstructor
public class ClassicRealmController {
    private final PriceService classicPriceService;

    @GetMapping("/{realmName}/price")
    public ResponseEntity<?> getPriceForRealm(@PathVariable String realmName,
                                              @RequestParam(defaultValue = "alliance") String faction) {
        return Faction.contains(faction) ?
                ResponseEntity.ok(classicPriceService.getPriceByRealmName(realmName, getFaction(faction)))
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid faction name: " + faction);
    }

    @GetMapping("/{realmName}/prices")
    public ResponseEntity<?> getAllPricesForRealm(@PathVariable String realmName,
                                                  @RequestParam(defaultValue = "alliance") String faction,
                                                  Pageable pageable) {
        return Faction.contains(faction) ?
                ResponseEntity.ok(classicPriceService.getAllPricesForRealm(realmName, getFaction(faction), pageable))
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid faction name: " + faction);
    }

    private Faction getFaction(String faction) {
        return Faction.valueOf(faction.toUpperCase());
    }

}