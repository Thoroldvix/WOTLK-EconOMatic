package com.thoroldvix.g2gcalculator.service;

import com.thoroldvix.g2gcalculator.dto.ItemPriceResponse;
import com.thoroldvix.g2gcalculator.dto.ItemResponse;
import com.thoroldvix.g2gcalculator.dto.PriceResponse;
import com.thoroldvix.g2gcalculator.model.Realm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
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
        int auctionHouseId = realm.getAuctionHouse().getId();
        ItemResponse item = classicAuctionHouseService.getAuctionHouseItem(auctionHouseId, itemId);
        PriceResponse updatedPrice = classicPriceService.getPriceForRealm(realm);

        long itemPrice = minBuyout ? item.minBuyout() : item.marketValue();
        ItemPriceResponse itemPriceResponse = classicItemPriceCalculator.calculatePrice(itemPrice, updatedPrice, amount);;
        return itemPriceResponse;
    }


}