package com.thoroldvix.pricepal.server.api;

import com.thoroldvix.pricepal.common.StringEnumConverter;
import com.thoroldvix.pricepal.server.dto.ServerResponse;
import com.thoroldvix.pricepal.server.entity.Region;
import com.thoroldvix.pricepal.server.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
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
        if (StringUtils.hasText(serverName)) {
            return ResponseEntity.ok(serverServiceImpl.getServerResponse(serverName));
        }
        return ResponseEntity.notFound().build();
    }


    @GetMapping
    public ResponseEntity<List<ServerResponse>> getAllServers() {
        return ResponseEntity.ok(serverServiceImpl.getAllServers());
    }

    @GetMapping("/regions/{regionName}")
    public ResponseEntity<?> getAllServersForRegion(@PathVariable String regionName) {
        Region region = StringEnumConverter.fromString(regionName, Region.class);
        return ResponseEntity.ok(serverServiceImpl.getAllServersForRegion(region));
    }

}