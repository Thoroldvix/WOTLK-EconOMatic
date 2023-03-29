package com.thoroldvix.g2gcalculator.service;

import com.thoroldvix.g2gcalculator.dto.ItemPriceResponse;
import com.thoroldvix.g2gcalculator.dto.ItemResponse;
import com.thoroldvix.g2gcalculator.dto.PriceResponse;
import com.thoroldvix.g2gcalculator.model.AuctionHouse;
import com.thoroldvix.g2gcalculator.model.Realm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClassicItemPriceServiceTest {
    @InjectMocks
    ClassicItemPriceService classicItemPriceService;
    @Mock
    private PriceService classicPriceService;
    @Mock
    private RealmService classicRealmService;
    @Mock
    private AuctionHouseService classicAuctionHouseService;
    @Mock
    private ItemPriceCalculator classicItemPriceCalculator;

    @Test
    void getPriceForItem_whenMinBuyOutFalse_returnsCorrectItemPriceResponse() {
        String realmName = "everlook-alliance";
        AuctionHouse auctionHouse = AuctionHouse.builder()
                .id(279)
                .build();
        Realm realm = Realm.builder()
                .name(realmName)
                .prices(new ArrayList<>())
                .auctionHouse(auctionHouse)
                .build();
        ItemResponse itemResponse = ItemResponse.builder()
                .itemId(1)
                .minBuyout(10000L)
                .marketValue(100000L)
                .build();
        ItemPriceResponse expectedResponse = ItemPriceResponse.builder()
                .price(BigDecimal.valueOf(10000))
                .build();
        PriceResponse priceResponse = PriceResponse.builder()
                .value(BigDecimal.valueOf(1000))
                .build();
        int itemId = 1;
        int amount = 1;
        boolean minBuyout = false;


        when(classicRealmService.getRealm(realmName)).thenReturn(realm);
        when(classicAuctionHouseService.getAuctionHouseItem(auctionHouse.getId(), itemId)).thenReturn(itemResponse);
        when(classicPriceService.getPriceForRealm(realm)).thenReturn(priceResponse);
        when(classicItemPriceCalculator.calculatePrice(itemResponse.marketValue(), priceResponse.value(), amount)).thenReturn(expectedResponse);


        ItemPriceResponse actualResponse = classicItemPriceService.getPriceForItem(realmName, itemId, amount, minBuyout);


        assertThat(actualResponse.price()).isEqualTo(expectedResponse.price());
    }

    @Test
    void getPriceForItem_whenMinBuyOutTrue_returnsCorrectItemPriceResponse() {
        String realmNAme = "everlook-alliance";
        AuctionHouse auctionHouse = AuctionHouse.builder()
                .id(279)
                .build();
        Realm realm = Realm.builder()
                .name(realmNAme)
                .prices(new ArrayList<>())
                .auctionHouse(auctionHouse)
                .build();
        ItemResponse itemResponse = ItemResponse.builder()
                .itemId(1)
                .minBuyout(10000L)
                .marketValue(100000L)
                .build();
        ItemPriceResponse expectedResponse = ItemPriceResponse.builder()
                .price(BigDecimal.valueOf(1000))
                .build();
        PriceResponse priceResponse = PriceResponse.builder()
                .value(BigDecimal.valueOf(1000))
                .build();
        int itemId = 1;
        int amount = 1;
        boolean minBuyout = true;


        when(classicRealmService.getRealm(realmNAme)).thenReturn(realm);
        when(classicAuctionHouseService.getAuctionHouseItem(auctionHouse.getId(), itemId)).thenReturn(itemResponse);
        when(classicPriceService.getPriceForRealm(realm)).thenReturn(priceResponse);
        when(classicItemPriceCalculator.calculatePrice(itemResponse.minBuyout(), priceResponse.value(), amount)).thenReturn(expectedResponse);


        ItemPriceResponse actualResponse = classicItemPriceService.getPriceForItem(realmNAme, itemId, amount, minBuyout);


        assertThat(actualResponse.price()).isEqualTo(expectedResponse.price());
    }


    @Test
    void getPriceForItem_ifAmountLessThanOne_throwsIllegalArgumentException() {
        String realmNAme = "test";
        int itemId = 1;
        int amount = 0;
        boolean minBuyout = false;
        assertThrows(IllegalArgumentException.class,
                () -> classicItemPriceService.getPriceForItem(realmNAme, itemId, amount, minBuyout));
    }

    @Test
    void getPriceForItem_ifItemIdLessThanOne_throwsIllegalArgumentException() {
        String realmNAme = "test";
        int itemId = 0;
        int amount = 1;
        boolean minBuyout = false;
        assertThrows(IllegalArgumentException.class,
                () -> classicItemPriceService.getPriceForItem(realmNAme, itemId, amount, minBuyout));
    }


}