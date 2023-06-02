package com.thoroldvix.pricepal.server.api;

import com.thoroldvix.pricepal.common.RequestDto;
import com.thoroldvix.pricepal.server.dto.ServerPriceResponse;
import com.thoroldvix.pricepal.server.dto.StatisticsResponse;
import com.thoroldvix.pricepal.server.service.ServerPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.thoroldvix.pricepal.common.BuildUtils.buildPageable;

@RestController
@RequestMapping("/wow-classic/api/v1/prices")
@RequiredArgsConstructor
public class ServerPriceController {

    private final ServerPriceService serverPriceService;

    @PostMapping("/statistics")
    public ResponseEntity<StatisticsResponse> getStatistics(@RequestBody RequestDto requestDto) {
        if (requestDto == null) {
            return ResponseEntity.badRequest().build();
        }
        StatisticsResponse statistics = serverPriceService.getStatisticsForSearch(requestDto);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/{serverName}")
    public ResponseEntity<List<ServerPriceResponse>> getPricesForServer(@PathVariable String serverName,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "100") int size,
                                                                        @RequestParam(defaultValue = "id,desc") String sort) {
        if (!StringUtils.hasText(serverName)) {
            return ResponseEntity.badRequest().build();
        }
        Pageable pageable = buildPageable(page, size, sort);
        List<ServerPriceResponse> prices = serverPriceService.getPricesForServer(serverName, pageable);
        return ResponseEntity.ok(prices);
    }

    @GetMapping("/{serverName}/statistics")
    public ResponseEntity<StatisticsResponse> getStatisticsForServer(@PathVariable String serverName) {
        if (!StringUtils.hasText(serverName)) {
            return ResponseEntity.badRequest().build();
        }
        StatisticsResponse statistics = serverPriceService.getStatisticsForServer(serverName);
        return ResponseEntity.ok(statistics);
    }


    @GetMapping
    public ResponseEntity<List<ServerPriceResponse>> getAllPrices(@RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "100") int size,
                                                                  @RequestParam(defaultValue = "id,desc") String sort) {
        Pageable pageable = buildPageable(page, size, sort);
        List<ServerPriceResponse> prices = serverPriceService.getAllPrices(pageable);
        return ResponseEntity.ok(prices);
    }

    @PostMapping("/search")
    public ResponseEntity<List<ServerPriceResponse>> searchForPrices(@RequestBody RequestDto requestDto,
                                                                     @RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "100") int size,
                                                                     @RequestParam(defaultValue = "id,desc") String sort) {
        if (requestDto == null) {
            return ResponseEntity.badRequest().build();
        }
        Pageable pageable = buildPageable(page, size, sort);
        List<ServerPriceResponse> prices = serverPriceService.searchForPrices(requestDto, pageable);
        return ResponseEntity.ok(prices);
    }


}