package com.thoroldvix.g2gcalculator.price;

import com.thoroldvix.g2gcalculator.g2g.G2GService;
import com.thoroldvix.g2gcalculator.server.Faction;
import com.thoroldvix.g2gcalculator.server.Server;
import com.thoroldvix.g2gcalculator.server.ServerResponse;
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

import java.math.BigDecimal;
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
    @Mock
    G2GService g2GService;

    @InjectMocks
    private PriceServiceImpl priceServiceImpl;

    
    @Test
    void getPriceForServer_whenServerNameProvided_returnsValidPriceResponse() {
        String serverName = "everlook-alliance";
        Server server = Server.builder()
                .prices(new ArrayList<>())
                .faction(Faction.ALLIANCE)
                .name("everlook")
                .build();
         Price price = Price.builder()
                .value(BigDecimal.valueOf(200))
                .updatedAt(LocalDateTime.now())
                .currency("USD")
                .build();
        PriceResponse priceResponse = PriceResponse.builder()
                .value(BigDecimal.valueOf(200))
                .build();

        when(serverServiceImpl.getServer(serverName)).thenReturn(server);
        when(priceRepository.findMostRecentPriceByServer(server)).thenReturn(Optional.of(price));
        when(priceMapper.toPriceResponse(any(Price.class))).thenReturn(priceResponse);
     
        PriceResponse result = priceServiceImpl.getPriceForServer(serverName);

        assertThat(result.value()).isEqualTo(priceResponse.value());
    }
     @Test
    void getPriceForServer_whenServerProvided_returnsValidPriceResponse() {
        Server server = Server.builder()
                .prices(new ArrayList<>())
                .faction(Faction.ALLIANCE)
                .name("everlook")
                .build();
         Price price = Price.builder()
                .value(BigDecimal.valueOf(200))
                .updatedAt(LocalDateTime.now())
                .currency("USD")
                .build();
        PriceResponse priceResponse = PriceResponse.builder()
                .value(BigDecimal.valueOf(200))
                .build();

        when(priceRepository.findMostRecentPriceByServer(server)).thenReturn(Optional.of(price));
        when(priceMapper.toPriceResponse(any(Price.class))).thenReturn(priceResponse);

        PriceResponse result = priceServiceImpl.getPriceForServer(server);

        assertThat(result.value()).isEqualTo(priceResponse.value());
    }
    @Test
    void getPriceForServer_whenServerIsNull_throwsNullPointerException() {
        Server server = null;
        assertThrows(NullPointerException.class, () -> priceServiceImpl.getPriceForServer(server));
    }
    @Test
    void getPriceForServer_whenServerNameIsNull_throwsIllegalArgumentException() {
        String serverName = null;
        assertThrows(IllegalArgumentException.class, () -> priceServiceImpl.getPriceForServer(serverName));
    }
     @Test
    void getPriceForServer_whenServerNameIsBlank_throwsIllegalArgumentException() {
        String serverName = " ";
        assertThrows(IllegalArgumentException.class, () -> priceServiceImpl.getPriceForServer(serverName));
    }





    @Test
    void getAllPricesForServer_returnsListOfPriceResponses() {
        Server server = new Server();
        String serverName = "everlook-alliance";
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

        when(serverServiceImpl.getServer(serverName)).thenReturn(server);
        Pageable pageable = PageRequest.of(0, 10);
        when(priceRepository.findAllByServer(server, pageable)).thenReturn(prices);
        when(priceMapper.toPriceResponse(firstPrice)).thenReturn(firstPriceResponse);
        when(priceMapper.toPriceResponse(secondPrice)).thenReturn(secondPriceResponse);

        List<PriceResponse> actualResponse = priceServiceImpl
                .getAllPricesForServer(serverName, pageable);

        assertThat(actualResponse).isEqualTo(expectedPriceResponses);
    }



    @Test
    void updatePrice_whenValidServerIdAndPriceResponseProvided_savesToPriceInDB() {
        Server server = Server.builder()
                .id(1)
                .prices(new ArrayList<>())
                .faction(Faction.ALLIANCE)
                .name("everlook")
                .build();
        Price price = Price.builder()
                .value(BigDecimal.valueOf(200))
                .updatedAt(LocalDateTime.now())
                .currency("USD")
                .build();
        PriceResponse priceResponse = PriceResponse.builder()
                .value(BigDecimal.valueOf(200))
                .build();

        when(serverServiceImpl.getServerById(1)).thenReturn(server);
        when(priceMapper.toPrice(priceResponse)).thenReturn(price);

        priceServiceImpl.savePrice(1, priceResponse);

        verify(priceRepository, times(1)).save(price);
    }
     @Test
    void updatePrice_whenPriceResponseIsNul_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> priceServiceImpl.savePrice(1, null));
    }
}