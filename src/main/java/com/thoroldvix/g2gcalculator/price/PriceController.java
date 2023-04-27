package com.thoroldvix.g2gcalculator.price;

import com.thoroldvix.g2gcalculator.common.ApiError;
import com.thoroldvix.g2gcalculator.item.dto.ItemPriceResponse;
import com.thoroldvix.g2gcalculator.item.price.ItemPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wow-classic/api/v1/prices")
@RequiredArgsConstructor
public class PriceController {

    private final PriceService priceServiceImpl;
    private final ItemPriceService itemPriceServiceImpl;

    @GetMapping("/{serverName}")
    public ResponseEntity<PriceResponse> getPriceForRealm(@PathVariable String serverName) {
        return ResponseEntity.ok(priceServiceImpl.getPriceForServer(serverName));
    }

    @GetMapping("/{serverName}/all")
    public ResponseEntity<List<PriceResponse>> getAllPricesForRealm(@PathVariable String serverName,
                                                                    Pageable pageable) {
        return ResponseEntity.ok(priceServiceImpl.getAllPricesForServer(serverName, pageable));
    }

    @GetMapping("/{serverName}/{itemName}")
    public ResponseEntity<ItemPriceResponse> getPriceForItem(@PathVariable String serverName,
                                                             @PathVariable String itemName,
                                                             @RequestParam(required = false, defaultValue = "1") Integer amount,
                                                             @RequestParam(required = false, defaultValue = "false") Boolean minBuyout) {

        return ResponseEntity.ok(itemPriceServiceImpl.getPriceForItem(serverName, itemName, amount, minBuyout));
    }
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiError> handleNullPointerException(NullPointerException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(HttpStatus.NOT_FOUND.value(), "Not found"));
    }
}