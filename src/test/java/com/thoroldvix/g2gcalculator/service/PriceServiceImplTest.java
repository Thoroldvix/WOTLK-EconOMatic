package com.thoroldvix.g2gcalculator.service;

import com.thoroldvix.g2gcalculator.price.PriceResponse;
import com.thoroldvix.g2gcalculator.price.PriceMapper;
import com.thoroldvix.g2gcalculator.server.Faction;
import com.thoroldvix.g2gcalculator.price.Price;
import com.thoroldvix.g2gcalculator.server.Server;
import com.thoroldvix.g2gcalculator.price.PriceServiceImpl;
import com.thoroldvix.g2gcalculator.price.PriceRepository;
import com.thoroldvix.g2gcalculator.server.ServerServiceImpl;
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
class PriceServiceImplTest {

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private ServerServiceImpl serverServiceImpl;




    @Mock
    private PriceMapper priceMapper;


    @InjectMocks
    private PriceServiceImpl priceServiceImpl;


    @Test
    void getPriceForRealmName_whenPriceIsUpToDate_returnsValidPriceResponse() {
        Server server = Server.builder()
                .prices(new ArrayList<>())
                .build();
        Price price = Price.builder()
                .value(BigDecimal.valueOf(100))
                .updatedAt(LocalDateTime.now())
                .server(server)
                .build();
        PriceResponse priceResponse = PriceResponse.builder()
                .value(BigDecimal.valueOf(100))
                .build();
        String fullRealmName = "everlook-alliance";

        ReflectionTestUtils.setField(priceServiceImpl, "priceUpdateInterval", Duration.ofHours(1));
        ReflectionTestUtils.setField(priceServiceImpl, "forceUpdate", false);


        when(serverServiceImpl.getServer(fullRealmName)).thenReturn(server);
        when(priceRepository.findMostRecentPriceByServer(server)).thenReturn(Optional.of(price));
        when(priceMapper.toPriceResponse(price)).thenReturn(priceResponse);


        PriceResponse result = priceServiceImpl.getPriceForRealmName(fullRealmName);

        assertThat(result.value()).isEqualTo(price.getValue());
    }

    @Test
    void getPriceForRealmName_whenPriceIsOld_returnsValidPriceResponse() {
        Server server = Server.builder()
                .prices(new ArrayList<>())
                .build();
        Price newPrice = Price.builder()
                .value(BigDecimal.valueOf(200))
                .updatedAt(LocalDateTime.now())
                .server(server)
                .build();
        Price oldPrice = Price.builder()
                .value(BigDecimal.valueOf(100))
                .updatedAt(LocalDateTime.now().minusHours(2))
                .server(server)
                .build();
        PriceResponse priceResponse = PriceResponse.builder()
                .value(BigDecimal.valueOf(200))
                .build();
        String fullRealmName = "everlook-alliance";

        ReflectionTestUtils.setField(priceServiceImpl, "priceUpdateInterval", Duration.ofHours(1));
        ReflectionTestUtils.setField(priceServiceImpl, "forceUpdate", false);

        when(classicScrapingService.fetchRealmPrice(server)).thenReturn(newPrice);
        when(serverServiceImpl.getServer(fullRealmName)).thenReturn(server);
        when(priceRepository.findMostRecentPriceByServer(server)).thenReturn(Optional.of(oldPrice));
        when(priceMapper.toPriceResponse(newPrice)).thenReturn(priceResponse);


        PriceResponse result = priceServiceImpl.getPriceForRealmName(fullRealmName);

        verify(priceRepository, times(1)).save(newPrice);
        assertThat(result.value()).isEqualTo(priceResponse.value());
    }


    @Test
    void getPriceForRealm_whenPriceIsUpToDate_returnsValidPriceResponse() {
        Server server = Server.builder()
                .prices(new ArrayList<>())
                .name("everlook")
                .faction(Faction.ALLIANCE)
                .build();
        Price price = Price.builder()
                .value(BigDecimal.valueOf(100))
                .updatedAt(LocalDateTime.now())
                .server(server)
                .build();
        PriceResponse priceResponse = PriceResponse.builder()
                .value(BigDecimal.valueOf(100))
                .build();

        ReflectionTestUtils.setField(priceServiceImpl, "priceUpdateInterval", Duration.ofHours(1));
        ReflectionTestUtils.setField(priceServiceImpl, "forceUpdate", false);


        when(priceRepository.findMostRecentPriceByServer(server)).thenReturn(Optional.of(price));
        when(priceMapper.toPriceResponse(price)).thenReturn(priceResponse);


        PriceResponse result = priceServiceImpl.getPriceForServer(server);

        assertThat(result.value()).isEqualTo(price.getValue());
    }

    @Test
    void getPriceForRealm_whenPriceIsOld_returnsValidPriceResponse() {
        Server server = Server.builder()
                .prices(new ArrayList<>())
                .faction(Faction.ALLIANCE)
                .name("everlook")
                .build();
        Price newPrice = Price.builder()
                .value(BigDecimal.valueOf(200))
                .updatedAt(LocalDateTime.now())
                .server(server)
                .build();
        Price oldPrice = Price.builder()
                .value(BigDecimal.valueOf(100))
                .updatedAt(LocalDateTime.now().minusHours(2))
                .server(server)
                .build();
        PriceResponse priceResponse = PriceResponse.builder()
                .value(BigDecimal.valueOf(200))
                .build();

        ReflectionTestUtils.setField(priceServiceImpl, "priceUpdateInterval", Duration.ofHours(1));
        ReflectionTestUtils.setField(priceServiceImpl, "forceUpdate", false);

        when(classicScrapingService.fetchRealmPrice(server)).thenReturn(newPrice);
        when(priceRepository.findMostRecentPriceByServer(server)).thenReturn(Optional.of(oldPrice));
        when(priceMapper.toPriceResponse(newPrice)).thenReturn(priceResponse);


        PriceResponse result = priceServiceImpl.getPriceForServer(server);

        verify(priceRepository, times(1)).save(newPrice);
        assertThat(result.value()).isEqualTo(priceResponse.value());
    }


    @Test
    void getAllPricesForRealm_returnsListOfPriceResponses() {
        Server server = new Server();
        String realmName = "everlook-alliance";
        Price firstPrice = Price.builder()
                .value(BigDecimal.valueOf(100))
                .server(server)
                .build();
        Price secondPrice = Price.builder()
                .value(BigDecimal.valueOf(200))
                .server(server)
                .build();
        Page<Price> prices = new PageImpl<>(List.of(firstPrice, secondPrice));
        PriceResponse firstPriceResponse = PriceResponse.builder()
                .value(BigDecimal.valueOf(100))
                .build();
        PriceResponse secondPriceResponse = PriceResponse.builder()
                .value(BigDecimal.valueOf(200))
                .build();
        List<PriceResponse> expectedPriceResponses = List.of(firstPriceResponse, secondPriceResponse);

        when(serverServiceImpl.getServer(realmName)).thenReturn(server);
        Pageable pageable = PageRequest.of(0, 10);
        when(priceRepository.findAllByServer(server, pageable)).thenReturn(prices);
        when(priceMapper.toPriceResponse(firstPrice)).thenReturn(firstPriceResponse);
        when(priceMapper.toPriceResponse(secondPrice)).thenReturn(secondPriceResponse);

        List<PriceResponse> actualResponse = priceServiceImpl
                .getAllPricesForServer(realmName, pageable);

        assertThat(actualResponse).isEqualTo(expectedPriceResponses);
    }


}