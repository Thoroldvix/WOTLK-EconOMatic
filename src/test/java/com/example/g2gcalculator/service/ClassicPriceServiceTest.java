package com.example.g2gcalculator.service;

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
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ClassicPriceServiceTest {

    @Mock
    private ClassicPriceRepository classicPriceRepository;

    @Mock
    private ClassicRealmRepository classicRealmRepository;

    @Mock
    private ScrapingService classicScrapingService;

    @Mock
    private PriceMapper priceMapper;

    @InjectMocks
    private ClassicPriceService priceService;

    @Test
    void getPriceByRealmId_returnsPriceResponse_whenPriceExistsForRealmId() {
        Integer realmId = 1;
        Price price = new Price();
        PriceResponse priceResponse = PriceResponse.builder().build();

        when(classicPriceRepository.findMostRecentPriceByRealm(any(Realm.class))).thenReturn(Optional.of(price));
        when(classicRealmRepository.findById(realmId)).thenReturn(Optional.of(new Realm()));
        when(priceMapper.toPriceResponse(price)).thenReturn(priceResponse);


        PriceResponse result = priceService.getPriceByRealmId(realmId);

        assertEquals(priceResponse, result);
    }

    @Test
    void getPriceByRealmId_throwsNotFoundException_whenPriceAndRealmDoesNotExist() {
        Integer realmId = 999;
        when(classicPriceRepository.findMostRecentPriceByRealm(any(Realm.class))).thenReturn(Optional.empty());
        when(classicRealmRepository.findById(realmId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> priceService.getPriceByRealmId(realmId));
    }

    @Test
    void getPriceByRealmId_returnsPriceResponse_whenPriceDoesNotExistsInDB() {
        Integer realmId = 1;
        BigDecimal priceValue = new BigDecimal(100);
        PriceResponse price = new PriceResponse(priceValue);

        Realm realm = new Realm();

        when(classicPriceRepository.findMostRecentPriceByRealm(realm)).thenReturn(Optional.empty());
        when(classicRealmRepository.findById(realmId)).thenReturn(Optional.of(realm));
        when(classicScrapingService.fetchRealmPrice(realm)).thenReturn(price);

        PriceResponse result = priceService.getPriceByRealmId(realmId);


        assertEquals(price, result);
    }

    @Test
    void getPriceByRealmId_savesNewRecentPrice_whenPriceDoesNotExistsInDB() {
        Integer realmId = 1;
        BigDecimal priceValue = new BigDecimal(100);
        PriceResponse price = new PriceResponse(priceValue);
        Realm realm = new Realm();
        Price savedPrice = new Price();
        savedPrice.setValue(priceValue);
        savedPrice.setRealm(realm);


        when(classicPriceRepository.findMostRecentPriceByRealm(realm)).thenReturn(Optional.empty());
        when(classicPriceRepository.save(any(Price.class))).thenReturn(savedPrice);
        when(classicRealmRepository.findById(realmId)).thenReturn(Optional.of(realm));
        when(classicScrapingService.fetchRealmPrice(realm)).thenReturn(price);

        priceService.getPriceByRealmId(realmId);

        verify(classicPriceRepository, times(1)).save(any(Price.class));
    }


    @Test
    void getAllPricesForRealm_returnsListOfPriceResponses() {
        Integer realmId = 1;
        Pageable pageable = PageRequest.of(0, 10);
        Price price = new Price();
        PriceResponse priceResponse = PriceResponse.builder().build();
        Page<Price> pricePage = new PageImpl<>(List.of(price));

        when(classicPriceRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pricePage);
        when(priceMapper.toPriceResponse(price)).thenReturn(priceResponse);

        List<PriceResponse> result = priceService.getAllPricesForRealm(realmId, pageable);

        assertEquals(List.of(priceResponse), result);
    }

}