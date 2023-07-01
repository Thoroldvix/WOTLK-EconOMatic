package com.thoroldvix.economatic.goldprice.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoroldvix.economatic.goldprice.dto.GoldPriceDeserializer;
import com.thoroldvix.economatic.goldprice.dto.GoldPriceResponse;
import com.thoroldvix.economatic.goldprice.error.G2GPriceNotFoundException;
import com.thoroldvix.economatic.goldprice.error.GoldPriceParsingException;
import com.thoroldvix.economatic.goldprice.model.GoldPrice;
import com.thoroldvix.economatic.goldprice.rest.G2GPriceClient;
import com.thoroldvix.economatic.server.model.Server;
import com.thoroldvix.economatic.server.repository.ServerRepository;
import com.thoroldvix.economatic.server.dto.ServerResponse;
import com.thoroldvix.economatic.server.service.ServerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.thoroldvix.economatic.shared.util.Utils.elapsedTimeInMillis;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoldPriceUpdateService {
    public static final String UPDATE_ON_STARTUP_OR_DEFAULT = "#{${economatic.update-on-startup} ? -1 : ${economatic.gold-price.update-rate}}";
    public static final String UPDATE_RATE = "${economatic.gold-price.update-rate}";
    private final ServerRepository serverRepository;
    private final G2GPriceClient g2gPriceClient;
    private final ServerService serverServiceImpl;
    private final GoldPriceService goldPriceServiceImpl;

    @Scheduled(fixedRateString = UPDATE_RATE,
            initialDelayString = UPDATE_ON_STARTUP_OR_DEFAULT,
            timeUnit = TimeUnit.MINUTES)
    @Retryable(maxAttempts = 5)
    protected  void update() {
        log.info("Updating gold prices");
        Instant start = Instant.now();

        List<GoldPrice> pricesToSave = retrieveGoldPrices(g2gPriceClient.getAllPrices());
        goldPriceServiceImpl.saveAll(pricesToSave);

        log.info("Finished updating gold prices in {} ms", elapsedTimeInMillis(start));
    }

    private List<GoldPrice> retrieveGoldPrices(String goldPricesJson) {
        return getPriceList(serverServiceImpl.getAll(), extractFromJson(goldPricesJson));
    }

    private List<GoldPrice> getPriceList(List<ServerResponse> servers, List<GoldPriceResponse> prices) {
        return servers.stream()
                .map(server -> getForServer(server, prices))
                .toList();
    }

    private GoldPrice getForServer(ServerResponse server, List<GoldPriceResponse> prices) {
        BigDecimal price = findPriceForServer(prices, server.uniqueName());
        return mapToGoldPriceEntity(server, price);
    }

    private BigDecimal findPriceForServer(List<GoldPriceResponse> prices, String uniqueServerName) {
        return prices.stream()
                .filter(result -> result.server().equals(uniqueServerName))
                .findFirst()
                .map(GoldPriceResponse::price)
                .orElseThrow(() -> new G2GPriceNotFoundException("Price not found for server: " + uniqueServerName));
    }

    private GoldPrice mapToGoldPriceEntity(ServerResponse server, BigDecimal price) {
        Server serverEntity = serverRepository.getReferenceById(server.id());
        return GoldPrice.builder()
                .value(price)
                .server(serverEntity)
                .build();
    }

    private List<GoldPriceResponse> extractFromJson(String goldPricesJson) {
        GoldPriceDeserializer deserializer = new GoldPriceDeserializer();
        ObjectMapper mapper = new ObjectMapper();
        DeserializationContext context = mapper.getDeserializationContext();
        JsonParser parser;
        try {
            parser = mapper.getFactory().createParser(goldPricesJson);
            return deserializer.deserialize(parser, context);
        } catch (IOException e) {
            throw new GoldPriceParsingException("Error while parsing gold price json");
        }
    }
}