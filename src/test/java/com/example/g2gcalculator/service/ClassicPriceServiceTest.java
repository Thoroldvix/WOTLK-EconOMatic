package com.example.g2gcalculator.service;

import com.example.g2gcalculator.config.G2GProperties;
import com.example.g2gcalculator.dto.PriceResponse;
import com.example.g2gcalculator.error.NotFoundException;
import com.example.g2gcalculator.mapper.PriceMapper;
import com.example.g2gcalculator.model.Price;
import com.example.g2gcalculator.model.Realm;
import com.example.g2gcalculator.repository.ClassicPriceRepository;
import com.example.g2gcalculator.repository.ClassicRealmRepository;
import com.example.g2gcalculator.service.impl.ClassicPriceService;
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

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static com.example.g2gcalculator.util.TestUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ClassicPriceServiceTest {

    @Mock
    private ClassicPriceRepository classicPriceRepository;

    @Mock
    private ClassicRealmRepository classicRealmRepository;

    @Mock
    private ScrapingService classicScrapingService;

    @Mock
    private PriceMapper priceMapper;

    @Mock
    private G2GProperties g2gProperties;

    @InjectMocks
    private ClassicPriceService priceService;


    @Test
    void getPriceForRealm_whenPriceExistsInDB_returnsValidPriceResponse() {
        Realm realm = createRealm();
        Price price = createPrice(BigDecimal.valueOf(100), realm);
        PriceResponse priceResponse = createPriceResponse(BigDecimal.valueOf(100));
        String fullRealmName = getFullRealmName(realm);
        Duration scrapingInterval = Duration.ofMinutes(60);
        boolean forceUpdate = false;

        when(g2gProperties.getScrapingInterval()).thenReturn(scrapingInterval);
        when(g2gProperties.isForceUpdate()).thenReturn(forceUpdate);

        when(classicPriceRepository.findMostRecentPriceByRealm(realm)).thenReturn(Optional.of(price));
        when(classicRealmRepository.findByNameAndFaction(realm.getName(), realm.getFaction())).thenReturn(Optional.of(realm));
        when(priceMapper.toPriceResponse(price)).thenReturn(priceResponse);


        PriceResponse result = priceService.getPriceForRealm(fullRealmName);


        verify(classicScrapingService, never()).fetchRealmPrice(any(Realm.class));
        verify(classicPriceRepository, never()).save(any(Price.class));
        verify(classicPriceRepository, times(1)).findMostRecentPriceByRealm(any(Realm.class));

        assertThat(result.price()).isEqualTo(price.getPrice() + "/1k");
    }

    @Test
    void getPriceForRealm_whenRealmNameInvalid_throwsNotFoundException() {
        Realm realm = createRealm();
        realm.setName("invalid");
        String fullRealmName = getFullRealmName(realm);


        when(classicRealmRepository.findByNameAndFaction(realm.getName(), realm.getFaction())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> priceService.getPriceForRealm(fullRealmName));
    }

    @Test
    void getPriceForRealm_whenFactionInvalid_throwsNotFoundException() {
        Realm realm = createRealm();


        String fullRealmName = getFullRealmName(realm);

        verifyNoInteractions(classicRealmRepository);
        assertThrows(NotFoundException.class, () -> priceService.getPriceForRealm(fullRealmName));
    }

    @Test
    void getPriceForRealm_whenPriceDoesNotExistsInDB_callsScraperAndReturnsValidPriceResponse() {
        Realm realm = createRealm();
        Price price = createPrice();
        PriceResponse priceResponse = createPriceResponse();
        String fullRealmName = getFullRealmName(realm);
        Duration scrapingInterval = Duration.ofMinutes(60);
        boolean forceUpdate = false;
        when(g2gProperties.getScrapingInterval()).thenReturn(scrapingInterval);
        when(g2gProperties.isForceUpdate()).thenReturn(forceUpdate);

        when(classicPriceRepository.findMostRecentPriceByRealm(realm)).thenReturn(Optional.empty());
        when(classicRealmRepository.findByNameAndFaction(realm.getName(), realm.getFaction())).thenReturn(Optional.of(realm));
        when(classicScrapingService.fetchRealmPrice(realm)).thenReturn(price);
        when(priceMapper.toPriceResponse(price)).thenReturn(priceResponse);

        PriceResponse result = priceService.getPriceForRealm(fullRealmName);

        verify(classicScrapingService, times(1)).fetchRealmPrice(any(Realm.class));
        assertThat(result.price()).isEqualTo(price.getPrice() + "/1k");
    }

    @Test
    void getPriceForRealm_savesNewRecentPrice_whenPriceDoesNotExistsInDB() {
        Realm realm = createRealm();
        Price price = createPrice();
        PriceResponse priceResponse = createPriceResponse();
        String fullRealmName = getFullRealmName(realm);
        Duration scrapingInterval = Duration.ofMinutes(60);
        boolean forceUpdate = false;
        when(g2gProperties.getScrapingInterval()).thenReturn(scrapingInterval);
        when(g2gProperties.isForceUpdate()).thenReturn(forceUpdate);


        when(classicPriceRepository.findMostRecentPriceByRealm(realm)).thenReturn(Optional.empty());
        when(classicRealmRepository.findByNameAndFaction(realm.getName(), realm.getFaction())).thenReturn(Optional.of(realm));
        when(classicScrapingService.fetchRealmPrice(realm)).thenReturn(price);
        when(priceMapper.toPriceResponse(price)).thenReturn(priceResponse);

        priceService.getPriceForRealm(fullRealmName);

        verify(classicPriceRepository, times(1)).save(price);
    }

    @Test
    void getAllPricesForRealm_returnsListOfPriceResponses() {
        Realm realm = createRealm();
        PriceResponse priceResponse = createPriceResponse();
        String fullRealmName = getFullRealmName(realm);

        List<PriceResponse> expectedResponse = createPriceResponseList(10);

        Page<Price> pricePage = new PageImpl<>(createPriceList(10));


        Pageable pageable = PageRequest.of(0, 10);

        when(classicRealmRepository.findByNameAndFaction(realm.getName(), realm.getFaction())).thenReturn(Optional.of(realm));
        when(classicPriceRepository.findAllByRealm(realm, pageable)).thenReturn(pricePage);
        when(priceMapper.toPriceResponse(any(Price.class))).thenReturn(priceResponse);

        List<PriceResponse> actualResponse = priceService.getAllPricesForRealm(fullRealmName, pageable);

        verify(classicPriceRepository, times(1)).findAllByRealm(realm, pageable);
        assertThat(actualResponse.size()).isEqualTo(expectedResponse.size());
    }


    @Test
    void getAllPricesForRealm_whenRealmNameInvalid_throwsNotFoundException() {
        Realm realm = createRealm();
        realm.setName("invalid");
        String fullRealmName = getFullRealmName(realm);
        Pageable pageable = PageRequest.of(0, 10);

        when(classicRealmRepository.findByNameAndFaction(realm.getName(), realm.getFaction())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> priceService.getAllPricesForRealm(fullRealmName, pageable));
    }

    @Test
    void getAllPricesForRealm_whenFactionInvalid_throwsNotFoundException() {
        Realm realm = createRealm();
        realm.setFaction(null);
        String fullRealmName = getFullRealmName(realm);
        Pageable pageable = PageRequest.of(0, 10);

        verifyNoInteractions(classicRealmRepository);
        assertThrows(NotFoundException.class, () -> priceService.getAllPricesForRealm(fullRealmName, pageable));
    }

}