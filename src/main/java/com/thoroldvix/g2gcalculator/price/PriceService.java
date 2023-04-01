package com.thoroldvix.g2gcalculator.price;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PriceService {
    <T> PriceResponse  getPriceForServer(T server);

    List<PriceResponse> getAllPricesForServer(String realmName, Pageable pageable);

    void updatePrice(int serverId, PriceResponse recentPrice);

}