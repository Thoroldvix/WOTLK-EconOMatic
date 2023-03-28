package com.thoroldvix.g2gcalculator.service;

import com.thoroldvix.g2gcalculator.dto.PriceResponse;
import com.thoroldvix.g2gcalculator.model.Realm;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PriceService {


    PriceResponse getPriceForRealmName(String realmName);

    PriceResponse getPriceForRealm(Realm realm);

    List<PriceResponse> getAllPricesForRealm(String realmName, Pageable pageable);


}