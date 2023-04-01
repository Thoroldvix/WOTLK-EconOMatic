package com.thoroldvix.g2gcalculator.server;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/wow-classic/v1/servers")
@RequiredArgsConstructor
public class ServerController {

    private final ServerService serverServiceImpl;

    @GetMapping("/{realmName}")
    public ResponseEntity<ServerResponse> getRealm(@PathVariable String realmName) {
        return ResponseEntity.ok(serverServiceImpl.getServerResponse(realmName));
    }

    @GetMapping
    public ResponseEntity<List<ServerResponse>> getAllRealms(Pageable pageable) {
        return ResponseEntity.ok(serverServiceImpl.getAllServers(pageable));
    }
}