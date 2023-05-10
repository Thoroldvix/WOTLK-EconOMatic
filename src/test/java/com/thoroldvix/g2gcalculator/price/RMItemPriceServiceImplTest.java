package com.thoroldvix.g2gcalculator.price;

import com.thoroldvix.g2gcalculator.item.*;
import com.thoroldvix.g2gcalculator.item.dto.AuctionHouseInfo;
import com.thoroldvix.g2gcalculator.item.dto.RealMoneyItemPrice;
import com.thoroldvix.g2gcalculator.item.dto.ItemInfo;
import com.thoroldvix.g2gcalculator.item.price.RMItemPriceCalculator;
import com.thoroldvix.g2gcalculator.item.price.RMItemPriceServiceImpl;
import com.thoroldvix.g2gcalculator.server.Server;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RMItemPriceServiceImplTest {
    @InjectMocks
    RMItemPriceServiceImpl itemPriceServiceImpl;
    @Mock
    private PriceService priceServiceImpl;
    @Mock
    private ServerService serverServiceImpl;
    @Mock
    ItemService itemServiceImpl;
    @Mock
    private RMItemPriceCalculator RMItemPriceCalculatorImpl;

    @Test
    void getPriceForItem_whenMinBuyOutFalseAndItemNameProvided_returnsCorrectItemPriceResponse() {
        String serverName = "everlook-alliance";
        Server server = Server.builder()
                .name(serverName)
                .prices(new ArrayList<>())
                .build();
        AuctionHouseInfo auctionHouseInfo = AuctionHouseInfo.builder()
                .itemId(1)
                .minBuyout(10000L)
                .marketValue(100000L)
                .build();
        ItemInfo itemInfo = ItemInfo.builder()
                .auctionHouseInfo(auctionHouseInfo)
                .build();
        RealMoneyItemPrice expectedResponse = RealMoneyItemPrice.builder()
                .value(BigDecimal.valueOf(10000))
                .currency("USD")
                .build();
        PriceResponse priceResponse = PriceResponse.builder()
                .currency("USD")
                .value(BigDecimal.valueOf(1000))
                .build();
        String itemName = "item";
        int amount = 1;
        boolean minBuyout = false;

        when(itemServiceImpl.getItemByName(serverName, itemName)).thenReturn(itemInfo);
        when(serverServiceImpl.getServer(serverName)).thenReturn(server);
        when(priceServiceImpl.getPriceForServer(server)).thenReturn(priceResponse);
        when(RMItemPriceCalculatorImpl.calculatePrice(itemInfo.auctionHouseInfo().marketValue(), priceResponse, amount)).thenReturn(expectedResponse.value());


        RealMoneyItemPrice actualResponse = itemPriceServiceImpl.getPriceForItem(serverName, itemName, amount, minBuyout);


        assertThat(actualResponse.value()).isEqualTo(expectedResponse.value());
        assertThat(actualResponse.currency()).isEqualTo(expectedResponse.currency());
    }
     @Test
    void getPriceForItem_whenMinBuyOutFalseAndItemIdProvided_returnsCorrectItemPriceResponse() {
        String serverName = "everlook-alliance";
        Server server = Server.builder()
                .name(serverName)
                .prices(new ArrayList<>())
                .build();
        AuctionHouseInfo auctionHouseInfo = AuctionHouseInfo.builder()
                .itemId(1)
                .minBuyout(10000L)
                .marketValue(100000L)
                .build();
        ItemInfo itemInfo = ItemInfo.builder()
                .auctionHouseInfo(auctionHouseInfo)
                .build();
        RealMoneyItemPrice expectedResponse = RealMoneyItemPrice.builder()
                .value(BigDecimal.valueOf(10000))
                .currency("USD")
                .build();
        PriceResponse priceResponse = PriceResponse.builder()
                .currency("USD")
                .value(BigDecimal.valueOf(1000))
                .build();
        String itemId = "123";
        int amount = 1;
        boolean minBuyout = false;

        when(itemServiceImpl.getItemById(serverName, Integer.parseInt(itemId))).thenReturn(itemInfo);
        when(serverServiceImpl.getServer(serverName)).thenReturn(server);
        when(priceServiceImpl.getPriceForServer(server)).thenReturn(priceResponse);
        when(RMItemPriceCalculatorImpl.calculatePrice(itemInfo.auctionHouseInfo().marketValue(), priceResponse, amount)).thenReturn(expectedResponse.value());


        RealMoneyItemPrice actualResponse = itemPriceServiceImpl.getPriceForItem(serverName, itemId, amount, minBuyout);


        assertThat(actualResponse.value()).isEqualTo(expectedResponse.value());
        assertThat(actualResponse.currency()).isEqualTo(expectedResponse.currency());
    }
     @Test
    void getPriceForItem_whenMinBuyOutTrueAndItemIdProvided_returnsCorrectItemPriceResponse() {
        String serverName = "everlook-alliance";
        Server server = Server.builder()
                .name(serverName)
                .prices(new ArrayList<>())
                .build();
        AuctionHouseInfo auctionHouseInfo = AuctionHouseInfo.builder()
                .itemId(1)
                .minBuyout(10000L)
                .marketValue(100000L)
                .build();
        ItemInfo itemInfo = ItemInfo.builder()
                .auctionHouseInfo(auctionHouseInfo)
                .build();
        RealMoneyItemPrice expectedResponse = RealMoneyItemPrice.builder()
                .value(BigDecimal.valueOf(1))
                .currency("USD")
                .build();
        PriceResponse priceResponse = PriceResponse.builder()
                .currency("USD")
                .value(BigDecimal.valueOf(1000))
                .build();
        String itemId = "123";
        int amount = 1;
        boolean minBuyout = true;

        when(itemServiceImpl.getItemById(serverName, Integer.parseInt(itemId))).thenReturn(itemInfo);
        when(serverServiceImpl.getServer(serverName)).thenReturn(server);
        when(priceServiceImpl.getPriceForServer(server)).thenReturn(priceResponse);
        when(RMItemPriceCalculatorImpl.calculatePrice(anyLong(), any(PriceResponse.class), anyInt())).thenReturn(expectedResponse.value());


        RealMoneyItemPrice actualResponse = itemPriceServiceImpl.getPriceForItem(serverName, itemId, amount, minBuyout);


        assertThat(actualResponse.value()).isEqualTo(expectedResponse.value());
        assertThat(actualResponse.currency()).isEqualTo(expectedResponse.currency());
    }

    @Test
    void getPriceForItem_whenMinBuyOutTrueAndItemNameProvided_returnsCorrectItemPriceResponse() {
         String serverName = "everlook-alliance";
        Server server = Server.builder()
                .name(serverName)
                .prices(new ArrayList<>())
                .build();
        AuctionHouseInfo auctionHouseInfo = AuctionHouseInfo.builder()
                .itemId(1)
                .minBuyout(10000L)
                .marketValue(100000L)
                .build();
        ItemInfo itemInfo = ItemInfo.builder()
                .auctionHouseInfo(auctionHouseInfo)
                .build();
        RealMoneyItemPrice expectedResponse = RealMoneyItemPrice.builder()
                .value(BigDecimal.valueOf(1))
                .currency("USD")
                .build();
        PriceResponse priceResponse = PriceResponse.builder()
                .currency("USD")
                .value(BigDecimal.valueOf(1000))
                .build();
        String itemName = "item";
        int amount = 1;
        boolean minBuyout = true;

        when(itemServiceImpl.getItemByName(serverName, itemName)).thenReturn(itemInfo);
        when(serverServiceImpl.getServer(serverName)).thenReturn(server);
        when(priceServiceImpl.getPriceForServer(server)).thenReturn(priceResponse);
        when(RMItemPriceCalculatorImpl.calculatePrice(anyLong(), any(PriceResponse.class), anyInt())).thenReturn(expectedResponse.value());


        RealMoneyItemPrice actualResponse = itemPriceServiceImpl.getPriceForItem(serverName, itemName, amount, minBuyout);


        assertThat(actualResponse.value()).isEqualTo(expectedResponse.value());
        assertThat(actualResponse.currency()).isEqualTo(expectedResponse.currency());
    }


    @Test
    void getPriceForItem_ifAmountLessThanOne_throwsIllegalArgumentException() {
        String realmNAme = "test";
        String itemId = "1";
        int amount = 0;
        boolean minBuyout = false;
        assertThrows(IllegalArgumentException.class,
                () -> itemPriceServiceImpl.getPriceForItem(realmNAme, itemId, amount, minBuyout));
    }

    @Test
    void getPriceForItem_ifItemIdOrNameIsNull_throwsIllegalArgumentException() {
        String realmNAme = "test";
        int amount = 1;
        boolean minBuyout = false;
        assertThrows(IllegalArgumentException.class,
                () -> itemPriceServiceImpl.getPriceForItem(realmNAme, null, amount, minBuyout));
    }
    @Test
    void getPriceForItem_ifItemIdOrNameIsBlank_throwsIllegalArgumentException() {
        String realmNAme = "test";
        String itemId = " ";
        int amount = 1;
        boolean minBuyout = false;
        assertThrows(IllegalArgumentException.class,
                () -> itemPriceServiceImpl.getPriceForItem(realmNAme, itemId, amount, minBuyout));
    }


}