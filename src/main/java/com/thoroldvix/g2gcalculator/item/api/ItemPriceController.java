package com.thoroldvix.g2gcalculator.item.api;

import com.thoroldvix.g2gcalculator.item.dto.ItemPrice;
import com.thoroldvix.g2gcalculator.item.service.ItemPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/wow-classic/api/v1/items/prices")
@RestController
@RequiredArgsConstructor
public class ItemPriceController {

    private final ItemPriceService itemPriceServiceImpl;
    @GetMapping("/{serverName}")
    public ResponseEntity<List<ItemPrice>> getAllItemPrices(String serverName) {
        if (!StringUtils.hasText(serverName)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(itemPriceServiceImpl.getAllItemPrices(serverName));
    }
}
