package com.thoroldvix.pricepal.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoroldvix.pricepal.server.api.G2GPriceClient;
import com.thoroldvix.pricepal.server.dto.ServerPriceResponse;
import com.thoroldvix.pricepal.server.dto.ServerResponse;
import com.thoroldvix.pricepal.server.entity.Currency;
import com.thoroldvix.pricepal.server.entity.Faction;
import com.thoroldvix.pricepal.server.entity.Region;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class G2GServiceTest {

    @Mock
    G2GPriceClient g2GPriceClient;

    @Mock
    ServerService serverServiceImpl;

    @Mock
    ServerPriceService serverPriceServiceImpl;

    private ObjectMapper mapper;

    @InjectMocks
    G2GService g2GService;


    @Test
    void updateUSPrices_shouldWork() throws JsonProcessingException {
        String server1Name = "Server1 [US] - Alliance";
        String server2Name = "Server2 [OCE] - Horde";
        String regionId = "dfced32f-2f0a-4df5-a218-1e068cfadffa";

        ServerResponse server1 = ServerResponse.builder()
                .id(1)
                .name("Server1")
                .region(Region.US)
                .faction(Faction.ALLIANCE)
                .build();
        ServerResponse server2 = ServerResponse.builder()
                .id(2)
                .name("Server2")
                .region(Region.OCE)
                .faction(Faction.HORDE)
                .build();
        ServerPriceResponse price1 = ServerPriceResponse.builder()
                .serverName(server1Name)
                .currency(Currency.USD)
                .value(BigDecimal.valueOf(10))
                .build();
        ServerPriceResponse price2 = ServerPriceResponse.builder()
                .serverName(server2Name)
                .currency(Currency.USD)
                .value(BigDecimal.valueOf(20))
                .build();
        List<ServerResponse> usServers = List.of(server1, server2);
        List<ServerPriceResponse> usPrices = List.of(price1, price2);


        when(serverServiceImpl.getAllServersForRegion(Region.US)).thenReturn(usServers);
        when(g2GPriceClient.getPrices(regionId, Currency.USD)).thenReturn(mapper.writeValueAsString(usPrices));

        g2GService.updateUsPrices();

        verify(serverServiceImpl, times(1)).getAllServersForRegion(Region.US);
        verify(g2GPriceClient, times(1)).getPrices(eq(regionId), eq(Currency.USD));
        verify(serverPriceServiceImpl, times(1)).savePrice(eq(1), eq(price1));
        verify(serverPriceServiceImpl, times(1)).savePrice(eq(2), eq(price2));
    }

    @Test
    void updateEUPrices_shouldWork() throws JsonProcessingException {
        String server1Name = "Pyrewood Village [EU] - Alliance";
        String server2Name = "Everlook [DE] - Horde";
        String server3Name = "Flamegor [RU] - Horde";
        String server4Name = "Chromie [RU] - Alliance";
        String euRegionId = Region.EU.g2gId;
        String ruRegionId = Region.RU.g2gId;
        ServerResponse server1 = ServerResponse.builder()
                .id(1)
                .name("Pyrewood Village")
                .region(Region.EU)
                .faction(Faction.ALLIANCE)
                .build();
        ServerResponse server2 = ServerResponse.builder()
                .id(2)
                .name("Everlook")
                .region(Region.DE)
                .faction(Faction.HORDE)
                .build();
        ServerPriceResponse price1 = ServerPriceResponse.builder()
                .serverName(server1Name)
                .currency(Currency.USD)
                .value(BigDecimal.valueOf(10))
                .build();
        ServerPriceResponse price2 = ServerPriceResponse.builder()
                .serverName(server2Name)
                .currency(Currency.USD)
                .value(BigDecimal.valueOf(20))
                .build();

 ServerResponse server3 = ServerResponse.builder()
                .id(1)
                .name("Flamegor")
                .region(Region.RU)
                .faction(Faction.HORDE)
                .build();
        ServerResponse server4 = ServerResponse.builder()
                .id(2)
                .name("Chromie")
                .region(Region.RU)
                .faction(Faction.ALLIANCE)
                .build();
        ServerPriceResponse price3 = ServerPriceResponse.builder()
                .serverName(server3Name)
                .currency(Currency.USD)
                .value(BigDecimal.valueOf(10))
                .build();
        ServerPriceResponse price4 = ServerPriceResponse.builder()
                .serverName(server4Name)
                .currency(Currency.USD)
                .value(BigDecimal.valueOf(20))
                .build();
        List<ServerResponse> euServers = List.of(server1, server2, server3, server4);

        List<ServerPriceResponse> euPrices = List.of(price1, price2);
        List<ServerPriceResponse> ruPrices = List.of(price3, price4);


        when(serverServiceImpl.getAllServersForRegion(Region.EU)).thenReturn(euServers);
        when(g2GPriceClient.getPrices(euRegionId, Currency.USD)).thenReturn(mapper.writeValueAsString(euPrices));
        when(g2GPriceClient.getPrices(ruRegionId, Currency.USD)).thenReturn(mapper.writeValueAsString(ruPrices));


        g2GService.updateEuPrices();


        verify(serverServiceImpl, times(1)).getAllServersForRegion(Region.EU);
        verify(g2GPriceClient, times(1)).getPrices(eq(euRegionId), eq(Currency.USD));
        verify(serverPriceServiceImpl, times(1)).savePrice(eq(1), eq(price1));
        verify(serverPriceServiceImpl, times(1)).savePrice(eq(2), eq(price2));
    }


}