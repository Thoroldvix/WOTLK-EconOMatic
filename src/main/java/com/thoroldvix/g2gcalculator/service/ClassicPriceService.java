package com.thoroldvix.g2gcalculator.service;

import com.thoroldvix.g2gcalculator.dto.PriceResponse;
import com.thoroldvix.g2gcalculator.mapper.PriceMapper;
import com.thoroldvix.g2gcalculator.model.Price;
import com.thoroldvix.g2gcalculator.model.Realm;
import com.thoroldvix.g2gcalculator.repository.ClassicPriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClassicPriceService implements PriceService {

    private final ClassicPriceRepository classicPriceRepository;
    private final RealmService classicRealmService;
    private final ScrapingService classicScrapingService;
    private final PriceMapper priceMapper;
    @Value("${g2g.scraping-interval:PT1H}")
    private Duration priceUpdateInterval;
    @Value("${g2g.force-update:false}")
    private boolean forceUpdate;

    @Override
    @Transactional
    public PriceResponse getPriceForRealmName(String realmName) {
        Realm realm = classicRealmService.getRealm(realmName);
        Optional<Price> recentPrice = classicPriceRepository.findMostRecentPriceByRealm(realm);
        return updatePriceForRealm(realm, recentPrice);
    }
    @Override
    @Transactional
    public PriceResponse getPriceForRealm(Realm realm) {
        Objects.requireNonNull(realm, "Realm cannot be null");
        Optional<Price> recentPrice = classicPriceRepository.findMostRecentPriceByRealm(realm);
        return updatePriceForRealm(realm, recentPrice);
    }


    private PriceResponse updatePriceForRealm(Realm realm, Optional<Price> recentPrice) {
        Price price;
        if (!requiresUpdate(recentPrice)) {
            price = recentPrice.get();
        } else {
            price = fetchPrice(realm);
            classicPriceRepository.save(price);

        }
        return priceMapper.toPriceResponse(price);
    }


    private boolean requiresUpdate(Optional<Price> recentPrice) {
        return recentPrice.isEmpty() || forceUpdate || isPriceStale(recentPrice.get(), priceUpdateInterval);
    }

    @Override
    public List<PriceResponse> getAllPricesForRealm(String realmName, Pageable pageable) {
        Realm realm = classicRealmService.getRealm(realmName);
        return classicPriceRepository.findAllByRealm(realm, pageable).getContent().stream()
                .map(priceMapper::toPriceResponse)
                .toList();
    }

    private Price fetchPrice(Realm realm) {
        Price price = classicScrapingService.fetchRealmPrice(realm);
        price.setRealm(realm);
        return price;
    }

    private boolean isPriceStale(Price recentPrice, Duration priceUpdateInterval) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime updatedAt = recentPrice.getUpdatedAt();
        return now.isAfter(updatedAt.plus(priceUpdateInterval));
    }

}