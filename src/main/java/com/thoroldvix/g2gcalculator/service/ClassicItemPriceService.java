package com.thoroldvix.g2gcalculator.service;

import com.thoroldvix.g2gcalculator.dto.ItemPriceResponse;
import com.thoroldvix.g2gcalculator.dto.ItemResponse;
import com.thoroldvix.g2gcalculator.dto.PriceResponse;
import com.thoroldvix.g2gcalculator.model.Realm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClassicItemPriceService implements ItemPriceService {

    private static final BigDecimal ONE_GOLD_IN_COPPER = BigDecimal.valueOf(10000);

    private final PriceService classicPriceService;

    private final RealmService classicRealmService;

    private final AuctionHouseService classicAuctionHouseService;


    private ItemPriceResponse calculateItemPriceMinBo(ItemResponse itemResponse, PriceResponse priceResponse, int amount) {
        if (itemResponse == null || priceResponse == null) {
            throw new IllegalArgumentException("Item and priceResponse cannot be null");
        }
        BigDecimal priceOfItemInGold = convertPriceToGold(itemResponse.minBuyout());
        BigDecimal totalPrice = priceResponse.value().multiply(priceOfItemInGold).multiply(BigDecimal.valueOf(amount));
        log.debug("Calculated total priceResponse for itemResponse {} with minBuyout priceResponse {} and amount {} as {}",
                itemResponse.itemId(), itemResponse.minBuyout(), amount, totalPrice);

        return new ItemPriceResponse(totalPrice);
    }

    private ItemPriceResponse calculateItemPriceMVal(ItemResponse itemResponse, PriceResponse priceResponse, int amount) {
        if (itemResponse == null || priceResponse == null) {
            throw new IllegalArgumentException("Item and price cannot be null");
        }
        BigDecimal priceOfItemInGold = convertPriceToGold(itemResponse.marketValue());
        BigDecimal totalPrice = priceResponse.value().multiply(priceOfItemInGold).multiply(BigDecimal.valueOf(amount));
        log.debug("Calculated total price for itemResponse {} with market value {} and amount {} as {}",
                itemResponse.itemId(), itemResponse.marketValue(), amount, totalPrice);

        return new ItemPriceResponse(totalPrice);
    }

    @Override
    public ItemPriceResponse getPriceForItem(String realmName, int itemId, int amount, boolean minBuyout) {
        validateInputs(realmName, amount, itemId);
        Realm realm = classicRealmService.getRealm(realmName);
        ItemResponse item = getItemFromAuctionHouse(realm, itemId);
        PriceResponse updatedPrice = classicPriceService.getPriceForRealm(realm);
        log.info("Retrieved updated price {} for realm {} for item {} with amount {} and minBuyout set to {}",
                updatedPrice.value(), realmName, itemId, amount, minBuyout);

        return calculateItemPrice(item, updatedPrice, amount, minBuyout);
    }

    private BigDecimal convertPriceToGold(long priceInCopper) {
        return BigDecimal.valueOf(priceInCopper).divide(ONE_GOLD_IN_COPPER, RoundingMode.HALF_UP);
    }

    @Override
    public ItemPriceResponse calculateItemPrice(ItemResponse itemResponse, PriceResponse priceResponse, int amount, boolean minBuyout) {
        return minBuyout ? calculateItemPriceMinBo(itemResponse, priceResponse, amount)
                : calculateItemPriceMVal(itemResponse, priceResponse, amount);
    }

    private void validateInputs(String realmName, int amount, int itemId) {
        if (realmName == null) {
            throw new IllegalArgumentException("Realm name cannot be null");
        }
        if (amount < 1) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        if (itemId < 1) {
            throw new IllegalArgumentException("Item id must be greater than 0");
        }
        log.debug("Inputs validated for realmName {}, amount {}, and itemId {}", realmName, amount, itemId);
    }

    private ItemResponse getItemFromAuctionHouse(Realm realm, int itemId) {
        int auctionHouseId = realm.getAuctionHouse().getId();
        ItemResponse item = classicAuctionHouseService.getAuctionHouseItem(auctionHouseId, itemId);
        log.debug("Retrieved item {} from auction house with id {}", item.itemId(), auctionHouseId);
        return item;
    }

}