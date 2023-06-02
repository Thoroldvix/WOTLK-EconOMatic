package com.thoroldvix.pricepal.server.api;

import com.thoroldvix.pricepal.common.RequestDto;
import com.thoroldvix.pricepal.server.dto.ServerResponse;
import com.thoroldvix.pricepal.server.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wow-classic/api/v1/servers")
@RequiredArgsConstructor
public class ServerController {

    private final ServerService serverService;

    @GetMapping("/{serverIdentifier}")
    public ResponseEntity<ServerResponse> getServer(@PathVariable String serverIdentifier) {
        if (!StringUtils.hasText(serverIdentifier)) {
            return ResponseEntity.badRequest().build();
        }
        return NumberUtils.isCreatable(serverIdentifier)
                ? ResponseEntity.ok(serverService.getServerResponse(Integer.parseInt(serverIdentifier)))
                : ResponseEntity.ok(serverService.getServerResponse(serverIdentifier));
    }
     @PostMapping("/search")
    public ResponseEntity<List<ServerResponse>> searchServers(@RequestBody RequestDto requestDto) {
        if (requestDto == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(serverService.searchServers(requestDto));
    }

    @GetMapping
    public ResponseEntity<List<ServerResponse>> getAllServers() {
        return ResponseEntity.ok(serverService.getAllServers());
    }
}