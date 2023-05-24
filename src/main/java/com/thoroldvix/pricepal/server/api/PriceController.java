package com.thoroldvix.pricepal.server.api;

import com.thoroldvix.pricepal.common.ApiError;
import com.thoroldvix.pricepal.common.StringEnumConverter;
import com.thoroldvix.pricepal.server.dto.ServerPriceResponse;
import com.thoroldvix.pricepal.server.entity.Region;
import com.thoroldvix.pricepal.server.service.ServerPriceService;
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

    private final ServerPriceService serverPriceServiceImpl;


    @GetMapping("/{serverName}")
    public ResponseEntity<ServerPriceResponse> getPriceForServer(@PathVariable String serverName,
                                                                 @RequestParam(required = false) boolean isRegion) {
        if (isRegion) {
            Region region = StringEnumConverter.fromString(serverName, Region.class);
            return ResponseEntity.ok(serverPriceServiceImpl.getAvgPriceForRegion(region));
        }
        return ResponseEntity.ok(serverPriceServiceImpl.getPriceForServer(serverName));
    }

    @GetMapping("/{serverName}/all")
    public ResponseEntity<List<ServerPriceResponse>> getAllPricesForServer(@PathVariable String serverName,
                                                                           Pageable pageable) {
        return ResponseEntity.ok(serverPriceServiceImpl.getAllPricesForServer(serverName, pageable));
    }

    @GetMapping("/{serverName}/avg")
    public ResponseEntity<ServerPriceResponse> getAvgPriceForServer(@PathVariable String serverName) {
        return ResponseEntity.ok(serverPriceServiceImpl.getAvgPriceForServer(serverName));
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiError> handleNullPointerException(NullPointerException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(HttpStatus.NOT_FOUND.value(), "Not found"));
    }
}