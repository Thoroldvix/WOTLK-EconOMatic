package com.thoroldvix.g2gcalculator.price;

import com.thoroldvix.g2gcalculator.server.Server;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PriceService {


    PriceResponse getPriceForServerName(String realmName);

    PriceResponse getPriceForServer(Server server);

    List<PriceResponse> getAllPricesForServer(String realmName, Pageable pageable);


}