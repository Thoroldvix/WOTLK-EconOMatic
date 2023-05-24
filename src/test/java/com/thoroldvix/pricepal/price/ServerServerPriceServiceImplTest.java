package com.thoroldvix.pricepal.price;

import com.thoroldvix.pricepal.server.dto.ServerPriceResponse;
import com.thoroldvix.pricepal.server.entity.Faction;
import com.thoroldvix.pricepal.server.entity.PriceRepository;
import com.thoroldvix.pricepal.server.entity.Server;
import com.thoroldvix.pricepal.server.entity.ServerPrice;
import com.thoroldvix.pricepal.server.service.G2GService;
import com.thoroldvix.pricepal.server.service.ServerPriceMapper;
import com.thoroldvix.pricepal.server.service.ServerPriceServiceImpl;
import com.thoroldvix.pricepal.server.service.ServerServiceImpl;
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
class ServerServerPriceServiceImplTest {

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private ServerServiceImpl serverServiceImpl;

    @Mock
    private ServerPriceMapper serverPriceMapper;
    @Mock
    G2GService g2GService;

    @InjectMocks
    private ServerPriceServiceImpl priceServiceImpl;

    
    @Test
    void getPriceForServer_whenServerNameProvided_returnsValidPriceResponse() {
        String serverName = "everlook-alliance";
        Server server = Server.builder()
                .serverPrices(new ArrayList<>())
                .faction(Faction.ALLIANCE)
                .name("everlook")
                .build();
         ServerPrice serverPrice = ServerPrice.builder()
                .value(BigDecimal.valueOf(200))
                .lastUpdated(LocalDateTime.now())
                .currency("USD")
                .build();
        ServerPriceResponse serverPriceResponse = ServerPriceResponse.builder()
                .value(BigDecimal.valueOf(200))
                .build();

        when(serverServiceImpl.getServer(serverName)).thenReturn(server);
        when(priceRepository.findMostRecentPriceByServer(server)).thenReturn(Optional.of(serverPrice));
        when(serverPriceMapper.toPriceResponse(any(ServerPrice.class))).thenReturn(serverPriceResponse);
     
        ServerPriceResponse result = priceServiceImpl.getPriceForServer(serverName);

        assertThat(result.value()).isEqualTo(serverPriceResponse.value());
    }
     @Test
    void getPriceForServer_whenServerProvided_returnsValidPriceResponse() {
        Server server = Server.builder()
                .serverPrices(new ArrayList<>())
                .faction(Faction.ALLIANCE)
                .name("everlook")
                .build();
         ServerPrice serverPrice = ServerPrice.builder()
                .value(BigDecimal.valueOf(200))
                .lastUpdated(LocalDateTime.now())
                .currency("USD")
                .build();
        ServerPriceResponse serverPriceResponse = ServerPriceResponse.builder()
                .value(BigDecimal.valueOf(200))
                .build();

        when(priceRepository.findMostRecentPriceByServer(server)).thenReturn(Optional.of(serverPrice));
        when(serverPriceMapper.toPriceResponse(any(ServerPrice.class))).thenReturn(serverPriceResponse);

        ServerPriceResponse result = priceServiceImpl.getPriceForServer(server);

        assertThat(result.value()).isEqualTo(serverPriceResponse.value());
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
        ServerPrice firstServerPrice = ServerPrice.builder()
                .value(BigDecimal.valueOf(100))
                .server(server)
                .build();
        ServerPrice secondServerPrice = ServerPrice.builder()
                .value(BigDecimal.valueOf(200))
                .server(server)
                .build();
        Page<ServerPrice> prices = new PageImpl<>(List.of(firstServerPrice, secondServerPrice));
        ServerPriceResponse firstServerPriceResponse = ServerPriceResponse.builder()
                .value(BigDecimal.valueOf(100))
                .build();
        ServerPriceResponse secondServerPriceResponse = ServerPriceResponse.builder()
                .value(BigDecimal.valueOf(200))
                .build();
        List<ServerPriceResponse> expectedServerPriceResponseRespons = List.of(firstServerPriceResponse, secondServerPriceResponse);

        when(serverServiceImpl.getServer(serverName)).thenReturn(server);
        Pageable pageable = PageRequest.of(0, 10);
        when(priceRepository.findAllByServer(server, pageable)).thenReturn(prices);
        when(serverPriceMapper.toPriceResponse(firstServerPrice)).thenReturn(firstServerPriceResponse);
        when(serverPriceMapper.toPriceResponse(secondServerPrice)).thenReturn(secondServerPriceResponse);

        List<ServerPriceResponse> actualResponse = priceServiceImpl
                .getAllPricesForServer(serverName, pageable);

        assertThat(actualResponse).isEqualTo(expectedServerPriceResponseRespons);
    }



    @Test
    void updatePrice_whenValidServerIdAndPriceResponseProvided_savesToPriceInDB() {
        Server server = Server.builder()
                .id(1)
                .serverPrices(new ArrayList<>())
                .faction(Faction.ALLIANCE)
                .name("everlook")
                .build();
        ServerPrice serverPrice = ServerPrice.builder()
                .value(BigDecimal.valueOf(200))
                .lastUpdated(LocalDateTime.now())
                .currency("USD")
                .build();
        ServerPriceResponse serverPriceResponse = ServerPriceResponse.builder()
                .value(BigDecimal.valueOf(200))
                .build();

        when(serverServiceImpl.getServer(1)).thenReturn(server);
        when(serverPriceMapper.toPrice(serverPriceResponse)).thenReturn(serverPrice);

        priceServiceImpl.savePrice(1, serverPriceResponse);

        verify(priceRepository, times(1)).save(serverPrice);
    }
     @Test
    void updatePrice_whenPriceResponseIsNul_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> priceServiceImpl.savePrice(1, null));
    }
}