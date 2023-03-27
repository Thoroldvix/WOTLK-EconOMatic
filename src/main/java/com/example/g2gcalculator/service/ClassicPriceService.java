package com.example.g2gcalculator.service;

import com.example.g2gcalculator.dto.ItemPriceResponse;
import com.example.g2gcalculator.dto.ItemResponse;
import com.example.g2gcalculator.dto.PriceResponse;
import com.example.g2gcalculator.mapper.PriceMapper;
import com.example.g2gcalculator.model.Price;
import com.example.g2gcalculator.model.Realm;
import com.example.g2gcalculator.repository.ClassicPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClassicPriceService implements PriceService {

    private final ClassicPriceRepository classicPriceRepository;
    private final RealmService classicRealmService;
    private final AuctionHouseService classicAuctionHouseService;
    private final ScrapingService classicScrapingService;
    private final ItemService classicItemService;
    private final PriceMapper priceMapper;
    @Value("${g2g.scraping-interval:PT1H}")
    private Duration updateFrequency;
    @Value("${g2g.force-update:false}")
    private boolean forceUpdate;

    @Override
    @Transactional
    public PriceResponse getPriceForRealm(String realmName) {
        if (realmName == null) {
            throw new IllegalArgumentException("Realm name cannot be null");
        }
        Realm realm = classicRealmService.getRealm(realmName);
        return getUpdatedPriceForRealm(realm);
    }

    @Transactional
    public PriceResponse updatePriceForRealm(Realm realm, Optional<Price> recentPrice) {
        if (realm == null) {
            throw new IllegalArgumentException("Realm cannot be null");
        }
        Price price;
        if (recentPrice.isPresent() && !forceUpdate && !isOld(recentPrice.get(), updateFrequency)) {
            price = recentPrice.get();
        } else {
            price = fetchPrice(realm);
            classicPriceRepository.save(price);
        }
        return priceMapper.toPriceResponse(price);
    }


    @Override
    public List<PriceResponse> getAllPricesForRealm(String realmName, Pageable pageable) {
        if (realmName == null) {
            throw new IllegalArgumentException("Realm name cannot be null");
        }
        Realm realm = classicRealmService.getRealm(realmName);
        return classicPriceRepository.findAllByRealm(realm, pageable).getContent().stream()
                .map(priceMapper::toPriceResponse)
                .toList();
    }


    @Override
    @Transactional
    public ItemPriceResponse getPriceForItem(String realmName, int itemId, int amount, boolean minBuyout) {
        validateInputs(realmName, amount);
        Realm realm = classicRealmService.getRealm(realmName);
        ItemResponse item = getItemFromAuctionHouse(realm, itemId);
        PriceResponse updatedPrice = getUpdatedPriceForRealm(realm);

        return calculateItemPrice(item, updatedPrice, amount, minBuyout);
    }

    private ItemResponse getItemFromAuctionHouse(Realm realm, int itemId) {
        Integer auctionHouseId = realm.getAuctionHouse().getId();
        return classicAuctionHouseService.getAuctionHouseItem(auctionHouseId, itemId);
    }

    private PriceResponse getUpdatedPriceForRealm(Realm realm) {
        Optional<Price> recentPrice = classicPriceRepository.findMostRecentPriceByRealm(realm);
        return updatePriceForRealm(realm, recentPrice);
    }

    private ItemPriceResponse calculateItemPrice(ItemResponse item, PriceResponse price, int amount, boolean minBuyout) {
        if (minBuyout) {
            return classicItemService.calculateItemPriceMinBo(item, price, amount);
        } else {
            return classicItemService.calculateItemPriceMVal(item, price, amount);
        }
    }

    private void validateInputs(String realmName, int amount) {
        if (realmName == null) {
            throw new IllegalArgumentException("Realm name cannot be null");
        }
        if (amount < 1) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
    }

    private Price fetchPrice(Realm realm) {
        Price price = classicScrapingService.fetchRealmPrice(realm);
        price.setRealm(realm);
        return price;
    }

    private boolean isOld(Price recentPrice, Duration priceUpdateInterval) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime updatedAt = recentPrice.getUpdatedAt();
        return now.isAfter(updatedAt.plus(priceUpdateInterval));
    }
}