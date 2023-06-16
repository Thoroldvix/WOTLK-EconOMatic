package com.thoroldvix.pricepal.goldprice;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoroldvix.pricepal.server.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoldPriceUpdateService {
    public static final String EU_G2G_ID = "ac3f85c1-7562-437e-b125-e89576b9a38e";
    public static final String US_G2G_ID = "dfced32f-2f0a-4df5-a218-1e068cfadffa";
    public static final String RU_G2G_ID = "166fbf02-6d9a-45a0-9f74-ac3ba5a002b4";
    private final ServerRepository serverRepository;
    private final G2GPriceClient g2GPriceClient;
    private final ServerService serverServiceImpl;
    private final GoldPriceService goldPriceServiceImpl;

    private static BigDecimal findPriceForUniqueServerName(List<GoldPriceResponse> prices, String uniqueServerName) {
        return prices.stream().filter(result -> result.serverName().equals(uniqueServerName))
                .findFirst()
                .map(GoldPriceResponse::price)
                .orElseThrow(() -> new G2GPriceNotFoundException("Price not found for server: " + uniqueServerName));
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.HOURS)
    public void update() {
        log.info("Starting gold price update");
        Instant start = Instant.now();
        updateEu();
        updateUs();
        log.info("Finished gold price update in {} ms", Duration.between(start, Instant.now()).toMillis());
    }

    public void updateUs() {
        List<ServerResponse> usServers = serverServiceImpl.getAllForRegion(Region.US);
        String usPricesJson = g2GPriceClient.getPrices(US_G2G_ID);
        List<GoldPriceResponse> usPrices = extractFromJson(usPricesJson);
        List<GoldPrice> pricesToSave = getPricesToSave(usServers, usPrices);
        goldPriceServiceImpl.saveAll(pricesToSave);

    }

    public void updateEu() {
        List<ServerResponse> euServers = serverServiceImpl.getAllForRegion(Region.EU);
        String euPricesJson = g2GPriceClient.getPrices(EU_G2G_ID);
        String ruPricesJson = g2GPriceClient.getPrices(RU_G2G_ID);
        List<GoldPriceResponse> euPrices = extractFromJson(euPricesJson);
        List<GoldPriceResponse> ruPrices = extractFromJson(ruPricesJson);
        euPrices.addAll(ruPrices);
        List<GoldPrice> pricesToSave = getPricesToSave(euServers, euPrices);
        goldPriceServiceImpl.saveAll(pricesToSave);


    }

    private List<GoldPrice> getPricesToSave(List<ServerResponse> euServers, List<GoldPriceResponse> euPrices) {
        List<GoldPrice> pricesToSave = new ArrayList<>();
        euServers.forEach(server -> {
            GoldPrice price = getForServer(server, euPrices);
            pricesToSave.add(price);
        });
        return pricesToSave;
    }

    private GoldPrice getForServer(ServerResponse server, List<GoldPriceResponse> prices) {
        String uniqueServerName = server.uniqueName();
        BigDecimal price = findPriceForUniqueServerName(prices, uniqueServerName);
        Server serverEntity = serverRepository.getReferenceById(server.id());
        return GoldPrice.builder()
                .price(price)
                .server(serverEntity)
                .build();
    }

    private List<GoldPriceResponse> extractFromJson(String json) {
        G2GPriceListDeserializer deserializer = new G2GPriceListDeserializer();
        ObjectMapper mapper = new ObjectMapper();
        DeserializationContext context = mapper.getDeserializationContext();
        JsonParser parser;
        try {
            parser = mapper.getFactory().createParser(json);
            return deserializer.deserialize(parser, context);
        } catch (IOException e) {
            throw new GoldPriceParsingException("Error while parsing gold price json", e);
        }
    }
}