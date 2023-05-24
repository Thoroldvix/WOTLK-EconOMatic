package com.thoroldvix.pricepal.server.api;

import com.thoroldvix.pricepal.common.ApiError;
import com.thoroldvix.pricepal.server.dto.ServerResponse;
import com.thoroldvix.pricepal.server.entity.Region;
import com.thoroldvix.pricepal.server.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/wow-classic/api/v1/servers")
@RequiredArgsConstructor
public class ServerController {

    private final ServerService serverServiceImpl;

    @GetMapping("/{serverName}")
    public ResponseEntity<ServerResponse> getServer(@PathVariable String serverName) {
        return ResponseEntity.ok(serverServiceImpl.getServerResponse(serverName));
    }

    @GetMapping
    public ResponseEntity<List<ServerResponse>> getAllServers() {
        return ResponseEntity.ok(serverServiceImpl.getAllServers());
    }

    @GetMapping("/regions/{regions}")
    public ResponseEntity<?> getAllServersForRegion(@PathVariable List<String> regions) {
        if (!verifyRegions(regions))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(HttpStatus.BAD_REQUEST.value(), "Bad request"));

        List<Region> regionList = regions.stream().map(region -> Region.valueOf(region.toUpperCase())).toList();
        return ResponseEntity.ok(serverServiceImpl.getAllServersForRegion(regionList));
    }

    private boolean verifyRegions(List<String> regions) {
        for (String region : regions) {
            if (!Region.contains(region))
                return false;
        }
        return true;
    }
}