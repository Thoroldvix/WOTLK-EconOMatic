package com.thoroldvix.pricepal.server.api;

import com.thoroldvix.pricepal.common.ApiError;
import com.thoroldvix.pricepal.common.StringEnumConverter;
import com.thoroldvix.pricepal.server.dto.ServerPriceResponse;
import com.thoroldvix.pricepal.server.entity.Faction;
import com.thoroldvix.pricepal.server.entity.Region;
import com.thoroldvix.pricepal.server.service.ServerPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wow-classic/api/v1/prices")
@RequiredArgsConstructor
public class ServerPriceController {

    private final ServerPriceService serverPriceServiceImpl;

    @GetMapping("/{serverName}")
    public ResponseEntity<List<ServerPriceResponse>> getAllPricesForServer(@PathVariable String serverName,
                                                                           Pageable pageable) {
        if (!StringUtils.hasText(serverName)) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(serverPriceServiceImpl.getAllPricesForServer(serverName, pageable));
    }

    @GetMapping("/{serverName}/avg")
    public ResponseEntity<ServerPriceResponse> getAvgPriceForServer(@PathVariable String serverName) {
        if (!StringUtils.hasText(serverName)) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(serverPriceServiceImpl.getAvgPriceForServer(serverName));
    }

    @GetMapping("/regions/{regionName}")
    public ResponseEntity<List<ServerPriceResponse>> getAllPricesForRegion(@PathVariable String regionName, Pageable pageable) {
        Region region = StringEnumConverter.fromString(regionName, Region.class);
        return ResponseEntity.ok(serverPriceServiceImpl.getAllPricesForRegion(region,  pageable));
    }

    @GetMapping("/regions/{regionName}/avg")
    public ResponseEntity<ServerPriceResponse> getAvgPriceForRegion(@PathVariable String regionName) {
        Region region = StringEnumConverter.fromString(regionName, Region.class);
        return ResponseEntity.ok(serverPriceServiceImpl.getAvgPriceForRegion(region));
    }


    @GetMapping("/factions/{factionName}")
    public ResponseEntity<List<ServerPriceResponse>> getAllPricesForFaction(@PathVariable String factionName, Pageable pageable) {
        Faction faction = StringEnumConverter.fromString(factionName, Faction.class);
        return ResponseEntity.ok(serverPriceServiceImpl.getAllPricesForFaction(faction, pageable));
    }

    @GetMapping("/factions/{factionName}/avg")
    public ResponseEntity<ServerPriceResponse> getAvgPriceForFaction(@PathVariable String factionName) {
        Faction faction = StringEnumConverter.fromString(factionName, Faction.class);
        return ResponseEntity.ok(serverPriceServiceImpl.getAvgPriceForFaction(faction));
    }


    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiError> handleNullPointerException(NullPointerException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(HttpStatus.NOT_FOUND.value(), "Not found"));
    }

}