package com.thoroldvix.g2gcalculator.price;

import com.thoroldvix.g2gcalculator.server.Region;
import com.thoroldvix.g2gcalculator.server.Server;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface PriceService {

    PriceResponse getPriceForServer(Server server);

    PriceResponse getPriceForServer(String serverName);

    List<PriceResponse> getAllPricesForServer(String serverName, Pageable pageable);

    List<PriceResponse> getAllPricesForServer(String serverName);

    List<PriceResponse> getAllPricesForServer(int serverId);

    BigDecimal getAvgPriceForServer(String serverName);

    void savePrice(int serverId, PriceResponse recentPrice);

    BigDecimal getAvgPriceForRegion(Region region);
}