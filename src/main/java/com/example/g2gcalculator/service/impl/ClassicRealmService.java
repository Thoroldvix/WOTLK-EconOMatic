package com.example.g2gcalculator.service.impl;

import com.example.g2gcalculator.dto.RealmResponse;
import com.example.g2gcalculator.error.NotFoundException;
import com.example.g2gcalculator.mapper.RealmMapper;
import com.example.g2gcalculator.model.Faction;
import com.example.g2gcalculator.model.Realm;
import com.example.g2gcalculator.repository.ClassicRealmRepository;
import com.example.g2gcalculator.service.RealmService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.g2gcalculator.util.CalculatorUtils.getExactRealmName;
import static com.example.g2gcalculator.util.CalculatorUtils.getFaction;

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
        return classicRealmRepository.findByNameAndFaction(exactRealmName, faction)
                .map(realmMapper::toRealmResponse)
                .orElseThrow(() -> new NotFoundException("No realm found for name: " + exactRealmName + " and faction: " + faction));
    }
    @Override
    public Realm getRealm(String realmName) {
        String exactRealmName = getExactRealmName(realmName);
        Faction faction = getFaction(realmName);
        return classicRealmRepository.findByNameAndFaction(exactRealmName, faction)
                .orElseThrow(() -> new NotFoundException("No realm found for name: " + exactRealmName + " and faction: " + faction));
    }

}