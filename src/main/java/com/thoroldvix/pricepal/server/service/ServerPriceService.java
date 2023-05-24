package com.thoroldvix.pricepal.server.service;

import com.thoroldvix.pricepal.server.dto.ServerPriceResponse;
import com.thoroldvix.pricepal.server.entity.Region;
import com.thoroldvix.pricepal.server.entity.Server;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ServerPriceService {

    ServerPriceResponse getPriceForServer(Server server);

    ServerPriceResponse getPriceForServer(String serverName);

    List<ServerPriceResponse> getAllPricesForServer(String serverName, Pageable pageable);

    List<ServerPriceResponse> getAllPricesForServer(String serverName);

    List<ServerPriceResponse> getAllPricesForServer(int serverId);

    ServerPriceResponse getAvgPriceForServer(String serverName);

    void savePrice(int serverId, ServerPriceResponse recentPrice);

    ServerPriceResponse getAvgPriceForRegion(Region region);
}