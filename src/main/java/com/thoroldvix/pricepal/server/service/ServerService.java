package com.thoroldvix.pricepal.server.service;

import com.thoroldvix.pricepal.server.dto.FullPopulationResponse;
import com.thoroldvix.pricepal.server.dto.ServerResponse;
import com.thoroldvix.pricepal.server.entity.Faction;
import com.thoroldvix.pricepal.server.entity.Region;
import com.thoroldvix.pricepal.server.entity.Server;

import java.util.List;

public interface ServerService {
    List<ServerResponse> getAllServers();


    Server getServer(int id);
    ServerResponse getServerResponse(int id);


    List<ServerResponse> getAllServersForRegion(Region region);

    List<ServerResponse> searchServerByName(String filterText);

    ServerResponse getServerResponse(String serverName);

    void updatePopulationForServer(String serverName, FullPopulationResponse population);

    List<ServerResponse> getAllServersForFaction(Faction faction);

    List<ServerResponse> getAllServersForName(String serverName);
    Server getServer(String serverName);
}