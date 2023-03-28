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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
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
        if (realmName == null) {
            throw new IllegalArgumentException("Realm name cannot be null");
        }
        log.debug("Getting price for realm name: {}", realmName);
        Realm realm = classicRealmService.getRealm(realmName);
        Optional<Price> recentPrice = classicPriceRepository.findMostRecentPriceByRealm(realm);
        return updatePriceForRealm(realm, recentPrice);
    }

    @Transactional
    public PriceResponse getPriceForRealm(Realm realm) {
        if (realm == null) {
            throw new IllegalArgumentException("Realm cannot be null");
        }
        log.debug("Getting price for realm: {}", realm.getName());
        Optional<Price> recentPrice = classicPriceRepository.findMostRecentPriceByRealm(realm);
        return updatePriceForRealm(realm, recentPrice);
    }


    private PriceResponse updatePriceForRealm(Realm realm, Optional<Price> recentPrice) {
        if (realm == null) {
            throw new IllegalArgumentException("Realm cannot be null");
        }
        log.debug("Updating price for realm: {}", realm.getName());
        Price price;
        if (!requiresUpdate(recentPrice)) {
            price = recentPrice.get();
        } else {
            price = fetchPrice(realm);
            classicPriceRepository.save(price);
            log.debug("Saved new price for realm: {}", realm.getName());
        }
        return priceMapper.toPriceResponse(price);
    }


    private boolean requiresUpdate(Optional<Price> recentPrice) {
        boolean requiresUpdate = recentPrice.isEmpty() || forceUpdate || isPriceStale(recentPrice.get(), priceUpdateInterval);
        if (requiresUpdate) {
            log.debug("Price update is required.");
        } else {
            log.debug("No price update is required.");
        }
        return requiresUpdate;
    }

    @Override
    public List<PriceResponse> getAllPricesForRealm(String realmName, Pageable pageable) {
        if (realmName == null) {
            throw new IllegalArgumentException("Realm name cannot be null");
        }
        log.debug("Getting all prices for realm name: {} with page: {} and size: {}"
                , realmName, pageable.getPageNumber(), pageable.getPageSize());
        Realm realm = classicRealmService.getRealm(realmName);
        log.debug("Retrieving all prices for realm: {}", realmName);
        List<PriceResponse> priceResponses = classicPriceRepository.findAllByRealm(realm, pageable).getContent().stream()
                .map(priceMapper::toPriceResponse)
                .toList();
        log.debug("Retrieved {} prices for realm: {}", priceResponses.size(), realmName);
        return priceResponses;
    }

    private Price fetchPrice(Realm realm) {
        log.debug("Fetching price for realm: {}", realm.getName());
        Price price = classicScrapingService.fetchRealmPrice(realm);
        price.setRealm(realm);
        log.debug("Fetched price for realm: {}", realm.getName());
        return price;
    }

    private boolean isPriceStale(Price recentPrice, Duration priceUpdateInterval) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime updatedAt = recentPrice.getUpdatedAt();
        boolean isStale = now.isAfter(updatedAt.plus(priceUpdateInterval));
        if (isStale) {
            log.debug("Price is stale. Last updated at: {}, price update interval: {}", updatedAt, priceUpdateInterval);
        } else {
            log.debug("Price is not stale. Last updated at: {}, price update interval: {}", updatedAt, priceUpdateInterval);
        }
        return isStale;
    }

}