package com.thoroldvix.pricepal.server.service;

import com.thoroldvix.pricepal.server.dto.ServerPriceResponse;
import com.thoroldvix.pricepal.server.entity.Faction;
import com.thoroldvix.pricepal.server.entity.Region;
import com.thoroldvix.pricepal.server.entity.Server;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ServerPriceService {

    ServerPriceResponse getPriceForServer(Server server);
    ServerPriceResponse getPriceForServer(int serverId);

    ServerPriceResponse getPriceForServer(String serverName);

    List<ServerPriceResponse> getAllPricesForServer(String serverName, Pageable pageable);


    List<ServerPriceResponse> getAllPricesForServer(int serverId, Pageable pageable);

    ServerPriceResponse getAvgPriceForServer(String serverName);
    ServerPriceResponse getAvgPriceForServer(int serverId);

    void savePrice(int serverId, ServerPriceResponse recentPrice);

    ServerPriceResponse getAvgPriceForRegion(Region region);

    List<ServerPriceResponse> getAllPricesForRegion(Region region, Pageable pageable);

    List<ServerPriceResponse> getAllPricesForFaction(Faction faction, Pageable pageable);

    ServerPriceResponse getAvgPriceForFaction(Faction faction);
}