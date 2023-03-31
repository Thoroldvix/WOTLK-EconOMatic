package com.thoroldvix.g2gcalculator.service;

import com.thoroldvix.g2gcalculator.price.ItemPriceResponse;
import com.thoroldvix.g2gcalculator.item.ItemStats;
import com.thoroldvix.g2gcalculator.price.PriceResponse;
import com.thoroldvix.g2gcalculator.server.Server;
import com.thoroldvix.g2gcalculator.price.ItemPriceServiceImpl;
import com.thoroldvix.g2gcalculator.price.ItemPriceCalculator;
import com.thoroldvix.g2gcalculator.price.PriceService;
import com.thoroldvix.g2gcalculator.server.ServerService;
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
class ItemPriceServiceImplTest {
    @InjectMocks
    ItemPriceServiceImpl itemPriceServiceImpl;
    @Mock
    private PriceService classicPriceService;
    @Mock
    private ServerService classicServerService;

    @Mock
    private ItemPriceCalculator classicItemPriceCalculator;

    @Test
    void getPriceForItem_whenMinBuyOutFalse_returnsCorrectItemPriceResponse() {
        String realmName = "everlook-alliance";
        AuctionHouse auctionHouse = AuctionHouse.builder()
                .id(279)
                .build();
        Server server = Server.builder()
                .name(realmName)
                .prices(new ArrayList<>())
                .auctionHouse(auctionHouse)
                .build();
        ItemStats itemStats = ItemStats.builder()
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


        when(classicServerService.getServer(realmName)).thenReturn(server);
        when(classicAuctionHouseService.getAuctionHouseItem(auctionHouse.getId(), itemId)).thenReturn(itemStats);
        when(classicPriceService.getPriceForServer(server)).thenReturn(priceResponse);
        when(classicItemPriceCalculator.calculatePrice(itemStats.marketValue(), priceResponse.value(), amount)).thenReturn(expectedResponse);


        ItemPriceResponse actualResponse = itemPriceServiceImpl.getPriceForItemId(realmName, itemId, amount, minBuyout);


        assertThat(actualResponse.price()).isEqualTo(expectedResponse.price());
    }

    @Test
    void getPriceForItem_whenMinBuyOutTrue_returnsCorrectItemPriceResponse() {
        String realmNAme = "everlook-alliance";
        AuctionHouse auctionHouse = AuctionHouse.builder()
                .id(279)
                .build();
        Server server = Server.builder()
                .name(realmNAme)
                .prices(new ArrayList<>())
                .auctionHouse(auctionHouse)
                .build();
        ItemStats itemStats = ItemStats.builder()
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


        when(classicServerService.getServer(realmNAme)).thenReturn(server);
        when(classicAuctionHouseService.getAuctionHouseItem(auctionHouse.getId(), itemId)).thenReturn(itemStats);
        when(classicPriceService.getPriceForServer(server)).thenReturn(priceResponse);
        when(classicItemPriceCalculator.calculatePrice(itemStats.minBuyout(), priceResponse.value(), amount)).thenReturn(expectedResponse);


        ItemPriceResponse actualResponse = itemPriceServiceImpl.getPriceForItemId(realmNAme, itemId, amount, minBuyout);


        assertThat(actualResponse.price()).isEqualTo(expectedResponse.price());
    }


    @Test
    void getPriceForItem_ifAmountLessThanOne_throwsIllegalArgumentException() {
        String realmNAme = "test";
        int itemId = 1;
        int amount = 0;
        boolean minBuyout = false;
        assertThrows(IllegalArgumentException.class,
                () -> itemPriceServiceImpl.getPriceForItemId(realmNAme, itemId, amount, minBuyout));
    }

    @Test
    void getPriceForItem_ifItemIdLessThanOne_throwsIllegalArgumentException() {
        String realmNAme = "test";
        int itemId = 0;
        int amount = 1;
        boolean minBuyout = false;
        assertThrows(IllegalArgumentException.class,
                () -> itemPriceServiceImpl.getPriceForItemId(realmNAme, itemId, amount, minBuyout));
    }


}