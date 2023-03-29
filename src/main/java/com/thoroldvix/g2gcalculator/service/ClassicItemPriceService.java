package com.thoroldvix.g2gcalculator.service;

import com.thoroldvix.g2gcalculator.dto.ItemPriceResponse;
import com.thoroldvix.g2gcalculator.dto.ItemResponse;
import com.thoroldvix.g2gcalculator.dto.PriceResponse;
import com.thoroldvix.g2gcalculator.model.Realm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClassicItemPriceService implements ItemPriceService {


    private final PriceService classicPriceService;

    private final RealmService classicRealmService;

    private final AuctionHouseService classicAuctionHouseService;

    private final ItemPriceCalculator classicItemPriceCalculator;


    @Override
    @Transactional
    public ItemPriceResponse getPriceForItem(String realmName, int itemId, int amount, boolean minBuyout) {
        if (amount < 1 || itemId < 1) {
            throw new IllegalArgumentException("Realm name, amount, and item id must be valid");
        }
        Realm realm = classicRealmService.getRealm(realmName);
        ItemResponse item = getItemFromAuctionHouse(realm, itemId);
        PriceResponse updatedPrice = classicPriceService.getPriceForRealm(realm);

        return calculateItemPrice(item, updatedPrice, amount, minBuyout);
    }

    private ItemPriceResponse calculateItemPrice(ItemResponse itemResponse, PriceResponse priceResponse, int amount, boolean minBuyout) {
        return minBuyout ? classicItemPriceCalculator.calculatePrice(itemResponse.minBuyout(), priceResponse.value(), amount)
                : classicItemPriceCalculator.calculatePrice(itemResponse.marketValue(), priceResponse.value(), amount);
    }


    private ItemResponse getItemFromAuctionHouse(Realm realm, int itemId) {
        int auctionHouseId = realm.getAuctionHouse().getId();
        return classicAuctionHouseService.getAuctionHouseItem(auctionHouseId, itemId);
    }
}