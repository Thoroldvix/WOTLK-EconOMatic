package com.example.g2gcalculator.service;

import com.example.g2gcalculator.dto.ItemPriceResponse;
import com.example.g2gcalculator.dto.ItemResponse;
import com.example.g2gcalculator.dto.PriceResponse;
import com.example.g2gcalculator.mapper.PriceMapper;
import com.example.g2gcalculator.model.AuctionHouse;
import com.example.g2gcalculator.model.Price;
import com.example.g2gcalculator.model.Realm;
import com.example.g2gcalculator.repository.ClassicPriceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ClassicPriceServiceTest {

    @Mock
    private ClassicPriceRepository classicPriceRepository;

    @Mock
    private ClassicAuctionHouseService classicAuctionHouseService;
    @Mock
    private ClassicRealmService classicRealmService;

    @Mock
    private ScrapingService classicScrapingService;

    @Mock
    private ItemService classicItemService;
    @Mock
    private PriceMapper priceMapper;


    @InjectMocks
    private ClassicPriceService classicPriceService;


    @Test
    void getPriceForRealm_returnsValidPriceResponse() {
        Realm realm = Realm.builder()
                .prices(new ArrayList<>())
                .build();
        Price price = Price.builder()
                .value(BigDecimal.valueOf(100))
                .updatedAt(LocalDateTime.now())
                .realm(realm)
                .build();

        PriceResponse priceResponse = PriceResponse.builder()
                .value(BigDecimal.valueOf(100))
                .build();
        String fullRealmName = "Everlook-Alliance";

        ReflectionTestUtils.setField(classicPriceService, "updateFrequency", Duration.ofHours(1));
        ReflectionTestUtils.setField(classicPriceService, "forceUpdate", false);
        when(classicRealmService.getRealm(fullRealmName)).thenReturn(realm);
        when(priceMapper.toPriceResponse(price)).thenReturn(priceResponse);
        when(classicRealmService.getRealm(fullRealmName)).thenReturn(realm);
        when(classicPriceRepository.findMostRecentPriceByRealm(realm)).thenReturn(Optional.of(price));
        when(priceMapper.toPriceResponse(price)).thenReturn(priceResponse);


        PriceResponse result = classicPriceService.getPriceForRealm(fullRealmName);

        assertThat(result.value()).isEqualTo(price.getValue());
    }

    @Test
    void getPriceForRealm_whenRealmNameIsNull_throwsIllegalArgumentException() {
        String fullRealmName = null;
        assertThrows(IllegalArgumentException.class, () -> classicPriceService.getPriceForRealm(fullRealmName));
    }

    @Test
    void getAllPricesForRealm_returnsListOfPriceResponses() {
        Realm realm = new Realm();
        String realmName = "Everlook-Alliance";
        Price firstPrice = Price.builder()
                .value(BigDecimal.valueOf(100))
                .realm(realm)
                .build();
        Price secondPrice = Price.builder()
                .value(BigDecimal.valueOf(200))
                .realm(realm)
                .build();
        Page<Price> prices = new PageImpl<>(List.of(firstPrice, secondPrice));
        PriceResponse firstPriceResponse = PriceResponse.builder()
                .value(BigDecimal.valueOf(100))
                .build();
        PriceResponse secondPriceResponse = PriceResponse.builder()
                .value(BigDecimal.valueOf(200))
                .build();
        List<PriceResponse> expectedPriceResponses = List.of(firstPriceResponse, secondPriceResponse);

        when(classicRealmService.getRealm(realmName)).thenReturn(realm);
        Pageable pageable = PageRequest.of(0, 10);
        when(classicPriceRepository.findAllByRealm(realm, pageable)).thenReturn(prices);
        when(priceMapper.toPriceResponse(firstPrice)).thenReturn(firstPriceResponse);
        when(priceMapper.toPriceResponse(secondPrice)).thenReturn(secondPriceResponse);

        List<PriceResponse> actualResponse = classicPriceService
                .getAllPricesForRealm(realmName, pageable);

        assertThat(actualResponse).isEqualTo(expectedPriceResponses);
    }


    @Test
    void getAllPricesForRealm_whenRealmNameIsNull_throwsIllegalArgumentException() {
        String realmName = null;
        assertThrows(IllegalArgumentException.class,
                () -> classicPriceService.getAllPricesForRealm(realmName, PageRequest.of(0, 10)));
    }


    @Test
    void updatePriceForRealm_fetchesPriceAndSavesToRepository() {
        Realm realm = Realm.builder()
                .prices(new ArrayList<>())
                .build();
        Price price = Price.builder()
                .value(BigDecimal.valueOf(100))
                .updatedAt(LocalDateTime.now().minusHours(2))
                .realm(realm)
                .build();
        ;

        when(classicScrapingService.fetchRealmPrice(realm)).thenReturn(price);
        when(classicPriceRepository.save(price)).thenReturn(price);
        when(priceMapper.toPriceResponse(price)).thenReturn(PriceResponse.builder()
                .value(price.getValue())
                .build());

        ReflectionTestUtils.setField(classicPriceService, "updateFrequency", Duration.ofHours(1));
        ReflectionTestUtils.setField(classicPriceService, "forceUpdate", false);


        classicPriceService.updatePriceForRealm(realm, Optional.empty());
    }

    @Test
    void updatePriceForRealm_returnsExistingPrice() {
        Realm realm = Realm.builder()
                .prices(new ArrayList<>())
                .build();
        Price price = Price.builder()
                .value(BigDecimal.valueOf(100))
                .updatedAt(LocalDateTime.now())
                .realm(realm)
                .build();
        ;


        when(priceMapper.toPriceResponse(price)).thenReturn(PriceResponse.builder()
                .value(price.getValue())
                .build());

        ReflectionTestUtils.setField(classicPriceService, "updateFrequency", Duration.ofHours(1));
        ReflectionTestUtils.setField(classicPriceService, "forceUpdate", false);

        PriceResponse priceResponse = classicPriceService.updatePriceForRealm(realm, Optional.of(price));


        assertThat(priceResponse.value()).isEqualTo(price.getValue());
    }

    @Test
    void updatePriceForRealm_throwsExceptionWhenRealmIsNull() {
        Realm realm = null;
        Optional<Price> recentPrice = Optional.empty();

        ReflectionTestUtils.setField(classicPriceService, "updateFrequency", Duration.ofHours(1));
        ReflectionTestUtils.setField(classicPriceService, "forceUpdate", false);

        assertThrows(IllegalArgumentException.class, () -> classicPriceService.updatePriceForRealm(realm, recentPrice));
    }

    @Test
    void getPriceForItem_returnsCorrectItemPriceResponse() {
        String realmNAme = "everlook-alliance";
        AuctionHouse auctionHouse = AuctionHouse.builder()
                .id(279)
                .build();
        Realm realm = Realm.builder()
                .name(realmNAme)
                .prices(new ArrayList<>())
                .auctionHouse(auctionHouse)
                .build();
        Price price = Price.builder()
                .value(BigDecimal.valueOf(100))
                .updatedAt(LocalDateTime.now())
                .realm(realm)
                .build();
        ItemResponse expectedResponse = ItemResponse.builder()
                .itemId(1)
                .minBuyout(1000L)
                .build();
        ItemPriceResponse itemPriceResponse = ItemPriceResponse.builder()
                .price(BigDecimal.valueOf(1000))
                .build();
        PriceResponse priceResponse = PriceResponse.builder()
                .value(BigDecimal.valueOf(1000))
                .build();
        int itemId = 1;
        int amount = 1;
        boolean minBuyout = false;

        ReflectionTestUtils.setField(classicPriceService, "updateFrequency", Duration.ofHours(1));
        ReflectionTestUtils.setField(classicPriceService, "forceUpdate", false);
        when(classicRealmService.getRealm(realmNAme)).thenReturn(realm);
        when(classicAuctionHouseService.getAuctionHouseItem(auctionHouse.getId(), itemId)).thenReturn(expectedResponse);
        when(classicPriceRepository.findMostRecentPriceByRealm(realm)).thenReturn(Optional.of(price));
        when(priceMapper.toPriceResponse(price)).thenReturn(priceResponse);
        when(classicItemService.calculateItemPriceMVal(expectedResponse, priceResponse, amount))
                .thenReturn(itemPriceResponse);

        classicPriceService.getPriceForItem(realmNAme, itemId, amount, minBuyout);


        assertThat(itemPriceResponse.price()).isEqualTo(BigDecimal.valueOf(1000));
    }
     @Test
    void getPriceForItem_ifMinBuyoutTrue_callsCorrectCalculateMethod() {
        String realmNAme = "everlook-alliance";
        AuctionHouse auctionHouse = AuctionHouse.builder()
                .id(279)
                .build();
        Realm realm = Realm.builder()
                .name(realmNAme)
                .prices(new ArrayList<>())
                .auctionHouse(auctionHouse)
                .build();
        Price price = Price.builder()
                .value(BigDecimal.valueOf(100))
                .updatedAt(LocalDateTime.now())
                .realm(realm)
                .build();
        ItemResponse expectedResponse = ItemResponse.builder()
                .itemId(1)
                .minBuyout(1000L)
                .build();
        ItemPriceResponse itemPriceResponse = ItemPriceResponse.builder()
                .price(BigDecimal.valueOf(1000))
                .build();
        PriceResponse priceResponse = PriceResponse.builder()
                .value(BigDecimal.valueOf(1000))
                .build();
        int itemId = 1;
        int amount = 1;
        boolean minBuyout = true;

        ReflectionTestUtils.setField(classicPriceService, "updateFrequency", Duration.ofHours(1));
        ReflectionTestUtils.setField(classicPriceService, "forceUpdate", false);
        when(classicRealmService.getRealm(realmNAme)).thenReturn(realm);
        when(classicAuctionHouseService.getAuctionHouseItem(auctionHouse.getId(), itemId)).thenReturn(expectedResponse);
        when(classicPriceRepository.findMostRecentPriceByRealm(realm)).thenReturn(Optional.of(price));
        when(priceMapper.toPriceResponse(price)).thenReturn(priceResponse);
        when(classicItemService.calculateItemPriceMinBo(expectedResponse, priceResponse, amount))
                .thenReturn(itemPriceResponse);

        classicPriceService.getPriceForItem(realmNAme, itemId, amount, minBuyout);

        verify(classicItemService, times(1)).calculateItemPriceMinBo(expectedResponse, priceResponse, amount);
        assertThat(itemPriceResponse.price()).isEqualTo(BigDecimal.valueOf(1000));
    }
    @Test
    void getPriceForItem_ifRealmNameNull_throwsIllegalArgumentException() {
        String realmNAme = null;
        int itemId = 1;
        int amount = 1;
        boolean minBuyout = false;
        assertThrows(IllegalArgumentException.class,
                () -> classicPriceService.getPriceForItem(realmNAme, itemId, amount, minBuyout));
    }
    @Test
    void getPriceForItem_ifAmountLessThanOne_throwsIllegalArgumentException() {
        String realmNAme = "test";
        int itemId = 1;
        int amount = 0;
        boolean minBuyout = false;
        assertThrows(IllegalArgumentException.class,
                () -> classicPriceService.getPriceForItem(realmNAme, itemId, amount, minBuyout));
    }
}