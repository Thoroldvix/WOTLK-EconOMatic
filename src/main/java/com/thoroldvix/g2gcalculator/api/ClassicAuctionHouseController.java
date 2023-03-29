package com.thoroldvix.g2gcalculator.api;

import com.thoroldvix.g2gcalculator.error.ApiError;
import com.thoroldvix.g2gcalculator.service.AuctionHouseService;
import com.thoroldvix.g2gcalculator.validation.ValidAhID;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/wow-classic/v1/ah")
@RequiredArgsConstructor
@Validated
public class ClassicAuctionHouseController {

    private final AuctionHouseService classicAuctionHouseService;

    @GetMapping("/{auctionHouseId}")
    public ResponseEntity<?> getAllAuctionHouseItems(@PathVariable @ValidAhID Integer auctionHouseId) {
        return ResponseEntity.ok(classicAuctionHouseService.getAllItemsByAuctionHouseId(auctionHouseId));
    }

    @GetMapping("/{auctionHouseId}/items/{itemId}")
    public ResponseEntity<?> getAuctionHouseItem(@PathVariable @ValidAhID Integer auctionHouseId, @PathVariable Integer itemId) {
        if (itemId < 1){
              return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(HttpStatus.NOT_FOUND.value()));
        }
        return ResponseEntity.ok(classicAuctionHouseService.getAuctionHouseItem(auctionHouseId, itemId));
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolationException(ConstraintViolationException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(HttpStatus.NOT_FOUND.value()));
    }

}