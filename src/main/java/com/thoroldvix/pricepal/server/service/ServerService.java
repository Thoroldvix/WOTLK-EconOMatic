package com.thoroldvix.pricepal.server.service;

import com.thoroldvix.pricepal.server.dto.PopulationResponse;
import com.thoroldvix.pricepal.server.dto.ServerResponse;
import com.thoroldvix.pricepal.server.entity.Region;
import com.thoroldvix.pricepal.server.entity.Server;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ServerService {
    List<ServerResponse> getAllServers(Pageable pageable);
    List<ServerResponse> getAllServers();

    Server getServer(int id);
    ServerResponse getServerResponse(int id);

    List<ServerResponse> getAllServersForRegion(Region region);
    List<ServerResponse> getAllServersForRegion(List<Region> regions);

    List<ServerResponse> searchServerByName(String filterText);

    ServerResponse getServerResponse(String serverName);

    void updatePopulationForServer(String serverName, PopulationResponse population);

    Server getServer(String serverName);
}