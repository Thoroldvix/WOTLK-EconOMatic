package com.thoroldvix.pricepal.server.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoroldvix.pricepal.server.api.G2GPriceClient;
import com.thoroldvix.pricepal.server.dto.G2GPriceListDeserializer;
import com.thoroldvix.pricepal.server.dto.ServerPriceResponse;
import com.thoroldvix.pricepal.server.dto.ServerResponse;
import com.thoroldvix.pricepal.server.entity.Region;
import com.thoroldvix.pricepal.server.error.G2GPriceNotFoundException;
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

    private final ServerPriceService serverPriceServiceImpl;

    private final String currency = "USD";


    @Scheduled(fixedDelay = 15, timeUnit = TimeUnit.MINUTES)
    public void updateAllServerPrices() {
        updateEuPrices();
        updateUsPrices();
    }

    public void updateUsPrices() {
        log.info("Updating US prices");
        List<ServerResponse> usServers = serverServiceImpl.getAllServersForRegion(Region.US);

        String usPricesJson = g2GPriceClient.getPrices(US_REGION_ID, currency);
        List<ServerPriceResponse> usPrices = extractPricesFromJson(usPricesJson);

        usServers.forEach(server -> {
            String serverName = server.uniqueName();
            ServerPriceResponse price = findPriceInResponse(serverName, usPrices);
            serverPriceServiceImpl.savePrice(server.id(), price);
        });
        log.info("Finished updating US prices");
    }

    public void updateEuPrices() {
        log.info("Updating EU prices");
        List<ServerResponse> euServers = serverServiceImpl.getAllServersForRegion(Region.EU);

        String euPricesJson = g2GPriceClient.getPrices(EU_REGION_ID, currency);
        String ruPricesJson = g2GPriceClient.getPrices(RU_REGION_ID, currency);

        List<ServerPriceResponse> euPrices = extractPricesFromJson(euPricesJson);
        List<ServerPriceResponse> ruPrices = extractPricesFromJson(ruPricesJson);
        euPrices.addAll(ruPrices);

        euServers.forEach(server -> {
            String serverName = server.uniqueName();
            ServerPriceResponse price = findPriceInResponse(serverName, euPrices);
            serverPriceServiceImpl.savePrice(server.id(), price);
        });
        log.info("Finished updating EU prices");
    }

    private ServerPriceResponse findPriceInResponse(String serverName, List<ServerPriceResponse> prices) {
        return prices.stream().filter(result -> result.serverName().equals(serverName))
                .findFirst()
                .orElseThrow(() -> new G2GPriceNotFoundException("Price not found for server: " + serverName));
    }

    @SneakyThrows
    private List<ServerPriceResponse> extractPricesFromJson(String json) {
        G2GPriceListDeserializer deserializer = new G2GPriceListDeserializer();
        ObjectMapper mapper = new ObjectMapper();
        DeserializationContext context = mapper.getDeserializationContext();
        JsonParser parser = mapper.getFactory().createParser(json);

        return deserializer.deserialize(parser, context);
    }

}