package com.thoroldvix.pricepal.server.api;

import com.thoroldvix.pricepal.common.ApiError;
import com.thoroldvix.pricepal.server.dto.FilterRequest;
import com.thoroldvix.pricepal.server.dto.FilteredPriceResponse;
import com.thoroldvix.pricepal.server.dto.ServerPriceResponse;
import com.thoroldvix.pricepal.server.entity.ServerPrice;
import com.thoroldvix.pricepal.server.entity.ServerPriceSpecifications;
import com.thoroldvix.pricepal.server.service.ServerPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wow-classic/api/v1/prices")
@RequiredArgsConstructor
public class ServerPriceController {

    private final ServerPriceService serverPriceService;
    @GetMapping("/avg")
    public ResponseEntity<FilteredPriceResponse> getAvgPrice(@ModelAttribute FilterRequest filters) {
        Specification<ServerPrice> spec = buildSpec(filters);
        ServerPriceResponse avgPrice = serverPriceService.getAvgPrices(spec);
        FilteredPriceResponse filteredPriceResponse = FilteredPriceResponse.builder()
                .avgPrice(avgPrice)
                .filters(filters)
                .build();
        return ResponseEntity.ok(filteredPriceResponse);
    }
    @GetMapping("/max")
    public ResponseEntity<FilteredPriceResponse> getMaxPrice(@ModelAttribute FilterRequest filters) {
        Specification<ServerPrice> spec = buildSpec(filters);
        ServerPriceResponse maxPrice = serverPriceService.getMaxPrices(spec);
        FilteredPriceResponse filteredPriceResponse = FilteredPriceResponse.builder()
                .maxPrice(maxPrice)
                .filters(filters)
                .build();
        return ResponseEntity.ok(filteredPriceResponse);
    }
    @GetMapping("/min")
    public ResponseEntity<FilteredPriceResponse> getMinPrice(@ModelAttribute FilterRequest filters) {
        Specification<ServerPrice> spec = buildSpec(filters);
        ServerPriceResponse minPrice = serverPriceService.getMinPrices(spec);
        FilteredPriceResponse filteredPriceResponse = FilteredPriceResponse.builder()
                .minPrice(minPrice)
                .filters(filters)
                .build();
        return ResponseEntity.ok(filteredPriceResponse);
    }
    @GetMapping("/med")
    public ResponseEntity<FilteredPriceResponse> getMedianPrice(@ModelAttribute FilterRequest filters) {
        Specification<ServerPrice> spec = buildSpec(filters);
        ServerPriceResponse medianPrice = serverPriceService.getMedianPrices(spec);
        FilteredPriceResponse filteredPriceResponse = FilteredPriceResponse.builder()
                .medianPrice(medianPrice)
                .filters(filters)
                .build();
        return ResponseEntity.ok(filteredPriceResponse);
    }

    @GetMapping
    public ResponseEntity<FilteredPriceResponse> getFilteredPrices(@ModelAttribute FilterRequest filters,
                                                                       Pageable pageable) {
        Specification<ServerPrice> spec = buildSpec(filters);
        List<ServerPriceResponse> prices = serverPriceService.getFilteredPrices(spec, pageable);
        FilteredPriceResponse filteredPriceResponse = FilteredPriceResponse.builder()
                .prices(prices)
                .filters(filters)
                .build();
        return ResponseEntity.ok(filteredPriceResponse);
    }

    private Specification<ServerPrice> buildSpec(FilterRequest filters) {
        Specification<ServerPrice> spec = Specification.where(null);
        if (filters != null) {
            spec = spec.and(ServerPriceSpecifications.serverNameIsEqualTo(filters.serverName()))
                    .and(ServerPriceSpecifications.regionIsEqualTo(filters.region()))
                    .and(ServerPriceSpecifications.factionIsEqualTo(filters.faction()))
                    .and(ServerPriceSpecifications.serverPriceIsInTimeRange(filters.timeRange()))
                    .and(ServerPriceSpecifications.uniqueServerNameIsEqualTo(filters.uniqueServerName()));
        }
        return spec;
    }


    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiError> handleNullPointerException(NullPointerException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(HttpStatus.NOT_FOUND.value(), "Not found"));
    }

}