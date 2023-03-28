package com.thoroldvix.g2gcalculator.service;

import com.thoroldvix.g2gcalculator.dto.PriceResponse;
import com.thoroldvix.g2gcalculator.mapper.PriceMapper;
import com.thoroldvix.g2gcalculator.model.Faction;
import com.thoroldvix.g2gcalculator.model.Price;
import com.thoroldvix.g2gcalculator.model.Realm;
import com.thoroldvix.g2gcalculator.repository.ClassicPriceRepository;
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
    private ClassicRealmService classicRealmService;

    @Mock
    private ScrapingService classicScrapingService;


    @Mock
    private PriceMapper priceMapper;


    @InjectMocks
    private ClassicPriceService classicPriceService;


    @Test
    void getPriceForRealmName_whenPriceIsUpToDate_returnsValidPriceResponse() {
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
        String fullRealmName = "everlook-alliance";

        ReflectionTestUtils.setField(classicPriceService, "priceUpdateInterval", Duration.ofHours(1));
        ReflectionTestUtils.setField(classicPriceService, "forceUpdate", false);


        when(classicRealmService.getRealm(fullRealmName)).thenReturn(realm);
        when(classicPriceRepository.findMostRecentPriceByRealm(realm)).thenReturn(Optional.of(price));
        when(priceMapper.toPriceResponse(price)).thenReturn(priceResponse);


        PriceResponse result = classicPriceService.getPriceForRealmName(fullRealmName);

        assertThat(result.value()).isEqualTo(price.getValue());
    }

    @Test
    void getPriceForRealmName_whenPriceIsOld_returnsValidPriceResponse() {
        Realm realm = Realm.builder()
                .prices(new ArrayList<>())
                .build();
        Price newPrice = Price.builder()
                .value(BigDecimal.valueOf(200))
                .updatedAt(LocalDateTime.now())
                .realm(realm)
                .build();
        Price oldPrice = Price.builder()
                .value(BigDecimal.valueOf(100))
                .updatedAt(LocalDateTime.now().minusHours(2))
                .realm(realm)
                .build();
        PriceResponse priceResponse = PriceResponse.builder()
                .value(BigDecimal.valueOf(200))
                .build();
        String fullRealmName = "everlook-alliance";

        ReflectionTestUtils.setField(classicPriceService, "priceUpdateInterval", Duration.ofHours(1));
        ReflectionTestUtils.setField(classicPriceService, "forceUpdate", false);

        when(classicScrapingService.fetchRealmPrice(realm)).thenReturn(newPrice);
        when(classicRealmService.getRealm(fullRealmName)).thenReturn(realm);
        when(classicPriceRepository.findMostRecentPriceByRealm(realm)).thenReturn(Optional.of(oldPrice));
        when(priceMapper.toPriceResponse(newPrice)).thenReturn(priceResponse);


        PriceResponse result = classicPriceService.getPriceForRealmName(fullRealmName);

        verify(classicPriceRepository, times(1)).save(newPrice);
        assertThat(result.value()).isEqualTo(priceResponse.value());
    }

    @Test
    void getPriceForRealmName_whenRealmNameIsNull_throwsIllegalArgumentException() {
        String fullRealmName = null;
        assertThrows(IllegalArgumentException.class, () -> classicPriceService.getPriceForRealmName(fullRealmName));
    }

    @Test
    void getPriceForRealm_whenRealmIsNull_throwsIllegalArgumentException() {
        Realm realm = null;
        assertThrows(IllegalArgumentException.class, () -> classicPriceService.getPriceForRealm(realm));
    }

    @Test
    void getPriceForRealm_whenPriceIsUpToDate_returnsValidPriceResponse() {
        Realm realm = Realm.builder()
                .prices(new ArrayList<>())
                .name("everlook")
                .faction(Faction.ALLIANCE)
                .build();
        Price price = Price.builder()
                .value(BigDecimal.valueOf(100))
                .updatedAt(LocalDateTime.now())
                .realm(realm)
                .build();
        PriceResponse priceResponse = PriceResponse.builder()
                .value(BigDecimal.valueOf(100))
                .build();

        ReflectionTestUtils.setField(classicPriceService, "priceUpdateInterval", Duration.ofHours(1));
        ReflectionTestUtils.setField(classicPriceService, "forceUpdate", false);


        when(classicPriceRepository.findMostRecentPriceByRealm(realm)).thenReturn(Optional.of(price));
        when(priceMapper.toPriceResponse(price)).thenReturn(priceResponse);


        PriceResponse result = classicPriceService.getPriceForRealm(realm);

        assertThat(result.value()).isEqualTo(price.getValue());
    }

    @Test
    void getPriceForRealm_whenPriceIsOld_returnsValidPriceResponse() {
        Realm realm = Realm.builder()
                .prices(new ArrayList<>())
                .faction(Faction.ALLIANCE)
                .name("everlook")
                .build();
        Price newPrice = Price.builder()
                .value(BigDecimal.valueOf(200))
                .updatedAt(LocalDateTime.now())
                .realm(realm)
                .build();
        Price oldPrice = Price.builder()
                .value(BigDecimal.valueOf(100))
                .updatedAt(LocalDateTime.now().minusHours(2))
                .realm(realm)
                .build();
        PriceResponse priceResponse = PriceResponse.builder()
                .value(BigDecimal.valueOf(200))
                .build();

        ReflectionTestUtils.setField(classicPriceService, "priceUpdateInterval", Duration.ofHours(1));
        ReflectionTestUtils.setField(classicPriceService, "forceUpdate", false);

        when(classicScrapingService.fetchRealmPrice(realm)).thenReturn(newPrice);
        when(classicPriceRepository.findMostRecentPriceByRealm(realm)).thenReturn(Optional.of(oldPrice));
        when(priceMapper.toPriceResponse(newPrice)).thenReturn(priceResponse);


        PriceResponse result = classicPriceService.getPriceForRealm(realm);

        verify(classicPriceRepository, times(1)).save(newPrice);
        assertThat(result.value()).isEqualTo(priceResponse.value());
    }


    @Test
    void getAllPricesForRealm_returnsListOfPriceResponses() {
        Realm realm = new Realm();
        String realmName = "everlook-alliance";
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


}