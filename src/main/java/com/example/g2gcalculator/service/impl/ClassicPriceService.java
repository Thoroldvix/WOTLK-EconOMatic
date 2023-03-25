package com.example.g2gcalculator.service.impl;

import com.example.g2gcalculator.dto.PriceResponse;
import com.example.g2gcalculator.error.NotFoundException;
import com.example.g2gcalculator.mapper.PriceMapper;
import com.example.g2gcalculator.model.Faction;
import com.example.g2gcalculator.model.Price;
import com.example.g2gcalculator.model.Realm;
import com.example.g2gcalculator.repository.ClassicPriceRepository;
import com.example.g2gcalculator.repository.ClassicRealmRepository;
import com.example.g2gcalculator.service.PriceService;
import com.example.g2gcalculator.service.ScrapingService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static com.example.g2gcalculator.util.CalculatorUtils.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClassicPriceService implements PriceService {

    private final ClassicPriceRepository classicPriceRepository;
    private final ClassicRealmRepository classicRealmRepository;
    private final ScrapingService classicScrapingService;
    private final PriceMapper priceMapper;
    @Value("${g2g.scraping-interval}")
    private Duration updateFrequency;
    @Value("${g2g.force-update}")
    private boolean forceUpdate;

    @Override
    @Transactional
    public PriceResponse getPriceForRealm(String realmName) {
        String exactRealmName = getExactRealmName(realmName);
        Faction faction = getFaction(realmName);


        Realm realm = classicRealmRepository.findByNameAndFaction(exactRealmName, faction)
                .orElseThrow(() -> new NotFoundException("No realm found for name: " + exactRealmName + " and faction: " + faction));
        Optional<Price> recentPrice = classicPriceRepository.findMostRecentPriceByRealm(realm);

        if (recentPrice.isEmpty() || forceUpdate || checkIfOld(recentPrice.get(), updateFrequency)) {
            return priceMapper.toPriceResponse(updatePrice(realm));
        } else {
            return priceMapper.toPriceResponse(recentPrice.get());
        }
    }

    private Price updatePrice(Realm realm) {
        Price price = classicScrapingService.fetchRealmPrice(realm);
        realm.setPrice(price);
        classicPriceRepository.save(price);
        return price;
    }

    @Override
    public List<PriceResponse> getAllPricesForRealm(String realmName, Pageable pageable) {
        String exactRealmName = getExactRealmName(realmName);
        Faction faction = getFaction(realmName);

        Realm realm = classicRealmRepository.findByNameAndFaction(exactRealmName, faction)
                .orElseThrow(() -> new NotFoundException("No realm found for name: " + exactRealmName + " and faction: " + faction));

        return classicPriceRepository.findAllByRealm(realm, pageable).getContent().stream()
                .map(priceMapper::toPriceResponse)
                .toList();
    }


}