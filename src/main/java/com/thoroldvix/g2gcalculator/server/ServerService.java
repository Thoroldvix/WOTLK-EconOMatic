package com.thoroldvix.g2gcalculator.server;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ServerService {
    List<ServerResponse> getAllServers(Pageable pageable);
    List<ServerResponse> getAllServers();

    Server getServerById(int id);

    List<ServerResponse> getAllServersForRegion(Region region);
    List<ServerResponse> getAllServersForRegion(List<Region> regions);

    List<ServerResponse> getAllServersByName(String filterText);

    ServerResponse getServerResponse(String serverName);



    Server getServer(String serverName);
}