package com.thoroldvix.g2gcalculator.service;

import com.thoroldvix.g2gcalculator.dto.RealmResponse;
import com.thoroldvix.g2gcalculator.error.NotFoundException;
import com.thoroldvix.g2gcalculator.mapper.RealmMapper;
import com.thoroldvix.g2gcalculator.model.Faction;
import com.thoroldvix.g2gcalculator.model.Realm;
import com.thoroldvix.g2gcalculator.repository.ClassicRealmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClassicRealmService implements RealmService {

    private final ClassicRealmRepository classicRealmRepository;
    private final RealmMapper realmMapper;

    @Override
    public List<RealmResponse> getAllRealms(Pageable pageable) {
        return classicRealmRepository.findAll(pageable).getContent().stream()
                .map(realmMapper::toRealmResponse)
                .toList();
    }

    @Override
    public RealmResponse getRealmResponse(String realmName) {
        String exactRealmName = getExactRealmName(realmName);
        Faction faction = getFaction(realmName);
        Realm realm = classicRealmRepository.findByNameAndFaction(exactRealmName, faction)
                .orElseThrow(() -> new NotFoundException("No realm found for name: " + exactRealmName + " and faction: " + faction));
        return realmMapper.toRealmResponse(realm);
    }

    @Override
    public Realm getRealm(String realmName) {
        String exactRealmName = getExactRealmName(realmName);
        Faction faction = getFaction(realmName);
        return classicRealmRepository.findByNameAndFaction(exactRealmName, faction)
                .orElseThrow(() -> new NotFoundException("No realm found for name: " + exactRealmName + " and faction: " + faction));
    }

    private String getExactRealmName(String realmName) {
        if (realmName == null || realmName.isBlank()) {
            throw new IllegalArgumentException("Realm name cannot be null or empty");
        }
        return realmName.split("-")[0];
    }

    private Faction getFaction(String realmName) {
        if (realmName == null || realmName.isBlank()) {
            throw new IllegalArgumentException("Realm name cannot be null or empty");
        }
        String[] split = realmName.split("-");
        String faction = split[split.length - 1];
        if (!Faction.contains(faction)) {
            throw new NotFoundException("No faction found for name: " + faction);
        }
        return faction.equalsIgnoreCase("horde") ? Faction.HORDE : Faction.ALLIANCE;
    }

}