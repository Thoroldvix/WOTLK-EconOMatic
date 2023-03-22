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
import com.example.g2gcalculator.service.PriceSpecification;
import com.example.g2gcalculator.service.ScrapingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClassicPriceService implements PriceService {
    private final ClassicPriceRepository classicPriceRepository;
    private final ClassicRealmRepository classicRealmRepository;
    private final ScrapingService classicScrapingService;
    private final PriceMapper priceMapper;

    @Override
    @Transactional
    public PriceResponse getPriceByRealmName(String realmName, Faction faction) {
        String formattedRealmName = formatRealmName(realmName);

        Realm realm = classicRealmRepository.findByNameAndFaction(formattedRealmName, faction)
                .orElseThrow(() -> new NotFoundException("No realm found for name: " + formattedRealmName + " and faction: " + faction));
        Optional<Price> recentPrice = classicPriceRepository.findMostRecentPriceByRealm(realm);

        if (recentPrice.isEmpty() || isOneHourOld(recentPrice)) {
            Price priceDataForRealm = classicScrapingService.fetchRealmPrice(realm);
            priceDataForRealm.setRealm(realm);
            classicPriceRepository.save(priceDataForRealm);
            return priceMapper.toPriceResponse(priceDataForRealm);
        }
        else {
            return priceMapper.toPriceResponse(recentPrice.get());
        }
    }
    private String formatRealmName(String realmName) {
        String[] split = realmName.split("-");
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            sb.append(s.substring(0, 1).toUpperCase()).append(s.substring(1).toLowerCase()).append(" ");
        }
        return sb.toString().trim();
    }


    private boolean isOneHourOld(Optional<Price> recentPrice) {
        return recentPrice
                .map(price ->
                        price.getCreatedAt().isBefore(LocalDateTime.now().minusSeconds(3600)))
                .orElse(false);
    }


    @Override
    public List<PriceResponse> getAllPricesForRealm(String realmName, Faction faction,  Pageable pageable) {
         String formattedRealmName = formatRealmName(realmName);
        Realm realm = classicRealmRepository.findByNameAndFaction(formattedRealmName, faction)
                .orElseThrow(() -> new NotFoundException("No realm found for name: " + realmName + " and faction: " + faction));
        Specification<Price> spec = PriceSpecification.matchId(realm.getId());

        return classicPriceRepository.findAll(spec, pageable).getContent().stream()
                .map(priceMapper::toPriceResponse)
                .toList();
    }


}