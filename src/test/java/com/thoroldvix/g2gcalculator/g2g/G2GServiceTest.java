package com.thoroldvix.g2gcalculator.g2g;

import com.thoroldvix.g2gcalculator.price.PriceResponse;
import com.thoroldvix.g2gcalculator.price.PriceService;
import com.thoroldvix.g2gcalculator.server.Faction;
import com.thoroldvix.g2gcalculator.server.Region;
import com.thoroldvix.g2gcalculator.server.ServerResponse;
import com.thoroldvix.g2gcalculator.server.ServerService;
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
                .region(Region.US.name())
                .faction(Faction.ALLIANCE)
                .build();
        ServerResponse server2 = ServerResponse.builder()
                .id(2)
                .name("Server2")
                .region(Region.OCE.name())
                .faction(Faction.HORDE)
                .build();
        PriceResponse price1 = PriceResponse.builder()
                .serverName(server1Name)
                .currency("USD")
                .value(BigDecimal.valueOf(10))
                .build();
        PriceResponse price2 = PriceResponse.builder()
                .serverName(server2Name)
                .currency("USD")
                .value(BigDecimal.valueOf(20))
                .build();
        List<ServerResponse> usServers = List.of(server1, server2);

        G2GPriceListResponse usPrices = new G2GPriceListResponse(List.of(price1, price2));


        when(serverServiceImpl.getAllForRegion(Region.getUSRegions())).thenReturn(usServers);
        when(g2GPriceClient.getPrices(regionId, currency)).thenReturn(usPrices);


        g2GService.updateUSPrices();


        verify(serverServiceImpl, times(1)).getAllForRegion(Region.getUSRegions());
        verify(g2GPriceClient, times(1)).getPrices(eq(regionId), eq(currency));
        verify(priceServiceImpl, times(1)).updatePrice(eq(1), eq(price1));
        verify(priceServiceImpl, times(1)).updatePrice(eq(2), eq(price2));
    }

    @Test
    void updateEUPrices_shouldWork() {
        String server1Name = "Server1 [EU] - Alliance";
        String server2Name = "Server2 [DE] - Horde";
        String regionId = "ac3f85c1-7562-437e-b125-e89576b9a38e";
        String currency = "USD";
        ServerResponse server1 = ServerResponse.builder()
                .id(1)
                .name("Server1")
                .region(Region.EU.name())
                .faction(Faction.ALLIANCE)
                .build();
        ServerResponse server2 = ServerResponse.builder()
                .id(2)
                .name("Server2")
                .region(Region.DE.name())
                .faction(Faction.HORDE)
                .build();
        PriceResponse price1 = PriceResponse.builder()
                .serverName(server1Name)
                .currency("USD")
                .value(BigDecimal.valueOf(10))
                .build();
        PriceResponse price2 = PriceResponse.builder()
                .serverName(server2Name)
                .currency("USD")
                .value(BigDecimal.valueOf(20))
                .build();

        List<ServerResponse> euServers = List.of(server1, server2);

        G2GPriceListResponse euPrices = new G2GPriceListResponse(List.of(price1, price2));


        when(serverServiceImpl.getAllForRegion(Region.getEURegions())).thenReturn(euServers);
        when(g2GPriceClient.getPrices(regionId, currency)).thenReturn(euPrices);


        g2GService.updateEUPrices();


        verify(serverServiceImpl, times(1)).getAllForRegion(Region.getEURegions());
        verify(g2GPriceClient, times(1)).getPrices(eq(regionId), eq(currency));
        verify(priceServiceImpl, times(1)).updatePrice(eq(1), eq(price1));
        verify(priceServiceImpl, times(1)).updatePrice(eq(2), eq(price2));
    }


}