package com.thoroldvix.g2gcalculator.price;

import com.thoroldvix.g2gcalculator.common.ApiError;
import com.thoroldvix.g2gcalculator.common.StringEnumConverter;
import com.thoroldvix.g2gcalculator.server.Region;
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


    @GetMapping("/{serverName}")
    public ResponseEntity<PriceResponse> getPriceForServer(@PathVariable String serverName) {
        return ResponseEntity.ok(priceServiceImpl.getPriceForServer(serverName));
    }

    @GetMapping("/{serverName}/all")
    public ResponseEntity<List<PriceResponse>> getAllPricesForServer(@PathVariable String serverName,
                                                                     Pageable pageable) {
        return ResponseEntity.ok(priceServiceImpl.getAllPricesForServer(serverName, pageable));
    }

    @GetMapping("/{serverName}/avg")
    public ResponseEntity<PriceResponse> getAvgPriceForServer(@PathVariable String serverName) {
        return ResponseEntity.ok(priceServiceImpl.getAvgPriceForServer(serverName));
    }
    @GetMapping("{/regionName}")
    public ResponseEntity<PriceResponse> getAvgPriceForRegion(@PathVariable String regionName) {
        Region region = StringEnumConverter.fromString(regionName, Region.class);
        return ResponseEntity.ok(priceServiceImpl.getAvgPriceForRegion(region));
    }

    @GetMapping("/{serverName}/all")
    public ResponseEntity<List<PriceResponse>> getAllPricesForServer(@PathVariable String serverName
                                                                     ) {
        return ResponseEntity.ok(priceServiceImpl.getAllPricesForServer(serverName));
    }


    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiError> handleNullPointerException(NullPointerException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(HttpStatus.NOT_FOUND.value(), "Not found"));
    }
}