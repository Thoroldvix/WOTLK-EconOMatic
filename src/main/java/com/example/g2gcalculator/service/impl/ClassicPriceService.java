package com.example.g2gcalculator.service.impl;

import com.example.g2gcalculator.dto.ItemPriceResponse;
import com.example.g2gcalculator.dto.ItemResponse;
import com.example.g2gcalculator.dto.PriceResponse;
import com.example.g2gcalculator.mapper.PriceMapper;
import com.example.g2gcalculator.model.Price;
import com.example.g2gcalculator.model.Realm;
import com.example.g2gcalculator.repository.ClassicPriceRepository;
import com.example.g2gcalculator.service.AuctionHouseService;
import com.example.g2gcalculator.service.PriceService;
import com.example.g2gcalculator.service.ScrapingService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static com.example.g2gcalculator.util.CalculatorUtils.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClassicPriceService implements PriceService {

    private final ClassicPriceRepository classicPriceRepository;
    private final ClassicRealmService classicRealmService;

    private final AuctionHouseService classicAuctionHouseService;
    private final ScrapingService classicScrapingService;
    private final PriceMapper priceMapper;
    @Value("${g2g.scraping-interval:60}")
    @DurationUnit(ChronoUnit.MINUTES)
    private Duration updateFrequency;
    @Value("${g2g.force-update:false}")
    private boolean forceUpdate;

    @Override
    @Transactional
    public PriceResponse getPriceForRealm(String realmName) {
        Realm realm = classicRealmService.getRealm(realmName);

        Optional<Price> recentPrice = classicPriceRepository.findMostRecentPriceByRealm(realm);

        if (recentPrice.isEmpty() || forceUpdate || checkIfOld(recentPrice.get(), updateFrequency)) {
            return priceMapper.toPriceResponse(updatePrice(realm));
        } else {
            return priceMapper.toPriceResponse(recentPrice.get());
        }
    }

    protected Price updatePrice(Realm realm) {
        Price price = classicScrapingService.fetchRealmPrice(realm);
        realm.setPrice(price);
        classicPriceRepository.save(price);
        return price;
    }

    @Override
    public List<PriceResponse> getAllPricesForRealm(String realmName, Pageable pageable) {
        Realm realm = classicRealmService.getRealm(realmName);

        return classicPriceRepository.findAllByRealm(realm, pageable).getContent().stream()
                .map(priceMapper::toPriceResponse)
                .toList();
    }

    @Override
    @Transactional
    public ItemPriceResponse getPriceForItem(String realmName, Integer itemId) {
        Realm realm = classicRealmService.getRealm(realmName);
        ItemResponse item = classicAuctionHouseService.getAuctionHouseItem(realm.getAuctionHouse().getId(), itemId);
        PriceResponse price = getPriceForRealm(realmName);
        return calculateItemPrice(item, price);
    }


    private ItemPriceResponse  calculateItemPrice(ItemResponse item, PriceResponse price){
        BigDecimal result = price.value().multiply(BigDecimal.valueOf(item.minBuyout() / 10000));
        return new ItemPriceResponse(result);
    }
}