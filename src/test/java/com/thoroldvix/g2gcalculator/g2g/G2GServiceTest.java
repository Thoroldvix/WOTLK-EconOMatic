package com.thoroldvix.g2gcalculator.g2g;

import com.thoroldvix.g2gcalculator.server.api.G2GPriceClient;
import com.thoroldvix.g2gcalculator.server.dto.G2GPriceListResponse;
import com.thoroldvix.g2gcalculator.server.dto.ServerPrice;
import com.thoroldvix.g2gcalculator.server.dto.ServerResponse;
import com.thoroldvix.g2gcalculator.server.entity.Faction;
import com.thoroldvix.g2gcalculator.server.entity.Region;
import com.thoroldvix.g2gcalculator.server.service.G2GService;
import com.thoroldvix.g2gcalculator.server.service.PriceService;
import com.thoroldvix.g2gcalculator.server.service.ServerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class G2GServiceTest {

    @Mock
    G2GPriceClient g2GPriceClient;

    @Mock
    ServerService serverServiceImpl;

    @Mock
    PriceService priceServiceImpl;

    @InjectMocks
    G2GService g2GService;


    @Test
    void updateUSPrices_shouldWork() {
        String server1Name = "Server1 [US] - Alliance";
        String server2Name = "Server2 [OCE] - Horde";
        String regionId = "dfced32f-2f0a-4df5-a218-1e068cfadffa";
        String currency = "USD";
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
        ServerPrice price1 = ServerPrice.builder()
                .serverName(server1Name)
                .currency("USD")
                .value(BigDecimal.valueOf(10))
                .build();
        ServerPrice price2 = ServerPrice.builder()
                .serverName(server2Name)
                .currency("USD")
                .value(BigDecimal.valueOf(20))
                .build();
        List<ServerResponse> usServers = List.of(server1, server2);

        G2GPriceListResponse usPrices = new G2GPriceListResponse(List.of(price1, price2));


        when(serverServiceImpl.getAllServersForRegion(Region.getUSRegions())).thenReturn(usServers);
        when(g2GPriceClient.getPrices(regionId, currency)).thenReturn(usPrices);


        g2GService.updateUSPrices();


        verify(serverServiceImpl, times(1)).getAllServersForRegion(Region.getUSRegions());
        verify(g2GPriceClient, times(1)).getPrices(eq(regionId), eq(currency));
        verify(priceServiceImpl, times(1)).savePrice(eq(1), eq(price1));
        verify(priceServiceImpl, times(1)).savePrice(eq(2), eq(price2));
    }

    @Test
    void updateEUPrices_shouldWork() {
        String server1Name = "Pyrewood Village [EU] - Alliance";
        String server2Name = "Everlook [DE] - Horde";
        String server3Name = "Flamegor [RU] - Horde";
        String server4Name = "Chromie [RU] - Alliance";
        String euRegionId = Region.EU.g2gId;
        String ruRegionId = Region.RU.g2gId;
        String currency = "USD";
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
        ServerPrice price1 = ServerPrice.builder()
                .serverName(server1Name)
                .currency(currency)
                .value(BigDecimal.valueOf(10))
                .build();
        ServerPrice price2 = ServerPrice.builder()
                .serverName(server2Name)
                .currency(currency)
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
        ServerPrice price3 = ServerPrice.builder()
                .serverName(server3Name)
                .currency("USD")
                .value(BigDecimal.valueOf(10))
                .build();
        ServerPrice price4 = ServerPrice.builder()
                .serverName(server4Name)
                .currency("USD")
                .value(BigDecimal.valueOf(20))
                .build();
        List<ServerResponse> euServers = List.of(server1, server2, server3, server4);

        G2GPriceListResponse euPrices = new G2GPriceListResponse(new ArrayList<>(List.of(price1, price2)));
        G2GPriceListResponse ruPrices = new G2GPriceListResponse(new ArrayList<>(List.of(price3, price4)));


        when(serverServiceImpl.getAllServersForRegion(Region.getEURegions())).thenReturn(euServers);
        when(g2GPriceClient.getPrices(euRegionId, currency)).thenReturn(euPrices);
        when(g2GPriceClient.getPrices(ruRegionId, currency)).thenReturn(ruPrices);


        g2GService.updateEUPrices();


        verify(serverServiceImpl, times(1)).getAllServersForRegion(Region.getEURegions());
        verify(g2GPriceClient, times(1)).getPrices(eq(euRegionId), eq(currency));
        verify(priceServiceImpl, times(1)).savePrice(eq(1), eq(price1));
        verify(priceServiceImpl, times(1)).savePrice(eq(2), eq(price2));
    }


}