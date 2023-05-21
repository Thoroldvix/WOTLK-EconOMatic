package com.thoroldvix.g2gcalculator.price;

import com.thoroldvix.g2gcalculator.server.dto.ServerPrice;
import com.thoroldvix.g2gcalculator.server.entity.Faction;
import com.thoroldvix.g2gcalculator.server.entity.Price;
import com.thoroldvix.g2gcalculator.server.entity.PriceRepository;
import com.thoroldvix.g2gcalculator.server.entity.Server;
import com.thoroldvix.g2gcalculator.server.service.G2GService;
import com.thoroldvix.g2gcalculator.server.service.PriceMapper;
import com.thoroldvix.g2gcalculator.server.service.PriceServiceImpl;
import com.thoroldvix.g2gcalculator.server.service.ServerServiceImpl;
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
        ServerPrice serverPrice = ServerPrice.builder()
                .value(BigDecimal.valueOf(200))
                .build();

        when(serverServiceImpl.getServer(serverName)).thenReturn(server);
        when(priceRepository.findMostRecentPriceByServer(server)).thenReturn(Optional.of(price));
        when(priceMapper.toPriceResponse(any(Price.class))).thenReturn(serverPrice);
     
        ServerPrice result = priceServiceImpl.getPriceForServer(serverName);

        assertThat(result.value()).isEqualTo(serverPrice.value());
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
        ServerPrice serverPrice = ServerPrice.builder()
                .value(BigDecimal.valueOf(200))
                .build();

        when(priceRepository.findMostRecentPriceByServer(server)).thenReturn(Optional.of(price));
        when(priceMapper.toPriceResponse(any(Price.class))).thenReturn(serverPrice);

        ServerPrice result = priceServiceImpl.getPriceForServer(server);

        assertThat(result.value()).isEqualTo(serverPrice.value());
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
        ServerPrice firstServerPrice = ServerPrice.builder()
                .value(BigDecimal.valueOf(100))
                .build();
        ServerPrice secondServerPrice = ServerPrice.builder()
                .value(BigDecimal.valueOf(200))
                .build();
        List<ServerPrice> expectedServerPriceRespons = List.of(firstServerPrice, secondServerPrice);

        when(serverServiceImpl.getServer(serverName)).thenReturn(server);
        Pageable pageable = PageRequest.of(0, 10);
        when(priceRepository.findAllByServer(server, pageable)).thenReturn(prices);
        when(priceMapper.toPriceResponse(firstPrice)).thenReturn(firstServerPrice);
        when(priceMapper.toPriceResponse(secondPrice)).thenReturn(secondServerPrice);

        List<ServerPrice> actualResponse = priceServiceImpl
                .getAllPricesForServer(serverName, pageable);

        assertThat(actualResponse).isEqualTo(expectedServerPriceRespons);
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
        ServerPrice serverPrice = ServerPrice.builder()
                .value(BigDecimal.valueOf(200))
                .build();

        when(serverServiceImpl.getServerById(1)).thenReturn(server);
        when(priceMapper.toPrice(serverPrice)).thenReturn(price);

        priceServiceImpl.savePrice(1, serverPrice);

        verify(priceRepository, times(1)).save(price);
    }
     @Test
    void updatePrice_whenPriceResponseIsNul_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> priceServiceImpl.savePrice(1, null));
    }
}