package com.thoroldvix.g2gcalculator.server;

import com.thoroldvix.g2gcalculator.price.PriceResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ServerService {
    List<ServerResponse> getAllServers(Pageable pageable);

    Server getServerById(int id);

    List<ServerResponse> getAllForRegion(Region region);
    List<ServerResponse> getAllForRegion(List<Region> regions);

    List<ServerResponse> getAllServers();

    ServerResponse getServerResponse(String serverName);

    Server getServer(String serverName);
}