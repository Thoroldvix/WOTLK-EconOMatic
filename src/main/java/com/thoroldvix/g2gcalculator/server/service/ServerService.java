package com.thoroldvix.g2gcalculator.server.service;

import com.thoroldvix.g2gcalculator.server.dto.ServerResponse;
import com.thoroldvix.g2gcalculator.server.entity.Region;
import com.thoroldvix.g2gcalculator.server.entity.Server;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ServerService {
    List<ServerResponse> getAllServers(Pageable pageable);
    List<ServerResponse> getAllServers();

    Server getServerById(int id);
    ServerResponse getServerResponseById(int id);

    List<ServerResponse> getAllServersForRegion(Region region);
    List<ServerResponse> getAllServersForRegion(List<Region> regions);

    List<ServerResponse> getAllServersByName(String filterText);

    ServerResponse getServerResponse(String serverName);



    Server getServer(String serverName);
}