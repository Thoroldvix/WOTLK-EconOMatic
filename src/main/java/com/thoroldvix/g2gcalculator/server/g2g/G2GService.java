package com.thoroldvix.g2gcalculator.server.g2g;

import com.thoroldvix.g2gcalculator.price.PriceResponse;
import com.thoroldvix.g2gcalculator.price.PriceService;
import com.thoroldvix.g2gcalculator.server.Region;
import com.thoroldvix.g2gcalculator.server.ServerResponse;
import com.thoroldvix.g2gcalculator.server.ServerService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class G2GService {
    private static final String EU_REGION_ID = "ac3f85c1-7562-437e-b125-e89576b9a38e";
    private static final String US_REGION_ID = "dfced32f-2f0a-4df5-a218-1e068cfadffa";
    private static final String RU_REGION_ID = "166fbf02-6d9a-45a0-9f74-ac3ba5a002b4";

    private final G2GPriceClient g2GPriceClient;

    private final ServerService serverServiceImpl;

    private final PriceService priceServiceImpl;

    private final String currency = "USD";


    @SneakyThrows
    @Scheduled(fixedDelayString = "${g2g.price-update-interval:PT15M}", timeUnit = TimeUnit.MINUTES)
    public void updateAllServerPrices() {
        updateEUPrices();
        updateUSPrices();
    }

    public void updateUSPrices() {
        log.info("Updating US prices");
        List<ServerResponse> usServers = serverServiceImpl.getAllServersForRegion(Region.getUSRegions());
        List<PriceResponse> usPrices = g2GPriceClient.getPrices(US_REGION_ID, currency).prices();

        usServers.forEach(server -> {
            String serverName = formatServerName(server);
            PriceResponse price = findPriceInResponse(serverName, usPrices);
            priceServiceImpl.savePrice(server.id(), price);
        });
        log.info("Finished updating US prices");
    }

    public void updateEUPrices() {
        log.info("Updating EU prices");
        List<ServerResponse> euServers = serverServiceImpl.getAllServersForRegion(Region.getEURegions());

        List<PriceResponse> euPrices = g2GPriceClient.getPrices(EU_REGION_ID, currency).prices();
        List<PriceResponse> ruPrices = g2GPriceClient.getPrices(RU_REGION_ID, currency).prices();
        euPrices.addAll(ruPrices);

        euServers.forEach(server -> {
            String serverName = formatServerName(server);
            PriceResponse price = findPriceInResponse(serverName, euPrices);
            priceServiceImpl.savePrice(server.id(), price);
        });
        log.info("Finished updating EU prices");
    }

    private PriceResponse findPriceInResponse(String serverName, List<PriceResponse> prices) {
        return prices.stream().filter(result -> result.serverName().equals(serverName))
                .findFirst()
                .orElseThrow(() -> new G2GPriceNotFoundException("Price not found for server: " + serverName));
    }

    private String formatServerName(ServerResponse server) {
        return String.format("%s [%s] - %s", server.name(),
                server.region(), server.faction().toString());
    }
}