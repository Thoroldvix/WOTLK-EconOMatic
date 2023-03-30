package com.thoroldvix.g2gcalculator.server;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ServerService {
    List<ServerResponse> getAllServers(Pageable pageable);

    ServerResponse getServerResponse(String serverName);

    Server getServer(String serverName);
}