package com.thoroldvix.g2gcalculator.service;

import com.thoroldvix.g2gcalculator.dto.RealmResponse;
import com.thoroldvix.g2gcalculator.model.Realm;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RealmService {
    List<RealmResponse> getAllRealms(Pageable pageable);

    RealmResponse getRealmResponse(String realmName);

    Realm getRealm(String realmName);
}