package com.example.g2gcalculator.service;

import com.example.g2gcalculator.dto.PriceResponse;
import com.example.g2gcalculator.mapper.PriceMapper;
import com.example.g2gcalculator.model.Price;
import com.example.g2gcalculator.repository.ClassicPriceRepository;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ClassicPriceServiceTest {

    @Mock
    private ClassicPriceRepository ClassicPriceRepository;

    @Mock
    private PriceMapper priceMapper;

    @InjectMocks
    private ClassicPriceService priceService;

    @Test
    void getPriceByRealmId_returnsPriceResponse_whenPriceExistsForRealmId() {
        Integer realmId = 1;
        Price price = new Price();
        PriceResponse priceResponse = PriceResponse.builder().build();

        when(ClassicPriceRepository.findMostRecentByRealmId(realmId)).thenReturn(Optional.of(price));
        when(priceMapper.toPriceResponse(price)).thenReturn(priceResponse);

        PriceResponse result = priceService.getPriceByRealmId(realmId);

        assertEquals(priceResponse, result);
    }

    @Test
    void getPriceByRealmId_throwsException_whenNoPriceExistsForRealmId() {
        Integer realmId = 1;
        when(ClassicPriceRepository.findMostRecentByRealmId(realmId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> priceService.getPriceByRealmId(realmId));
    }

    @Test
    void getAllPricesForRealm_returnsListOfPriceResponses() {
            Integer realmId = 1;
            Pageable pageable = PageRequest.of(0, 10);
            Price price = new Price();
            PriceResponse priceResponse = PriceResponse.builder().build();
            Page<Price> pricePage = new PageImpl<>(List.of(price));

            when(ClassicPriceRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pricePage);
            when(priceMapper.toPriceResponse(price)).thenReturn(priceResponse);

            List<PriceResponse> result = priceService.getAllPricesForRealm(realmId, pageable);

            assertEquals(List.of(priceResponse), result);
    }

}