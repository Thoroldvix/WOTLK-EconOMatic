package com.example.g2gcalculator.api;

import com.example.g2gcalculator.dto.PriceResponse;
import com.example.g2gcalculator.service.PriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.g2gcalculator.util.CalculatorUtils.constructPageable;

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
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size,
                                                                    @RequestParam(defaultValue = "id,asc") String sort) {

        Pageable pageable = constructPageable(page, size, sort);
        return ResponseEntity.ok(classicPriceService.getAllPricesForRealm(realmName, pageable));
    }

}