package com.thoroldvix.g2gcalculator.server.service;

import com.thoroldvix.g2gcalculator.server.dto.ServerPrice;
import com.thoroldvix.g2gcalculator.server.entity.Region;
import com.thoroldvix.g2gcalculator.server.entity.Server;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PriceService {

    ServerPrice getPriceForServer(Server server);

    ServerPrice getPriceForServer(String serverName);

    List<ServerPrice> getAllPricesForServer(String serverName, Pageable pageable);

    List<ServerPrice> getAllPricesForServer(String serverName);

    List<ServerPrice> getAllPricesForServer(int serverId);

    ServerPrice getAvgPriceForServer(String serverName);

    void savePrice(int serverId, ServerPrice recentPrice);

    ServerPrice getAvgPriceForRegion(Region region);
}