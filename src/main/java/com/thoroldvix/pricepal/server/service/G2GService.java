package com.thoroldvix.pricepal.server.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoroldvix.pricepal.server.api.G2GPriceClient;
import com.thoroldvix.pricepal.server.dto.G2GPriceListDeserializer;
import com.thoroldvix.pricepal.server.dto.ServerPriceResponse;
import com.thoroldvix.pricepal.server.dto.ServerResponse;
import com.thoroldvix.pricepal.server.entity.Region;
import com.thoroldvix.pricepal.server.entity.Server;
import com.thoroldvix.pricepal.server.entity.ServerPrice;
import com.thoroldvix.pricepal.server.error.G2GPriceNotFoundException;
import com.thoroldvix.pricepal.server.repository.ServerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class G2GService {

    private final ServerRepository serverRepository;

    private final G2GPriceClient g2GPriceClient;

    private final ServerService serverServiceImpl;

    private final ServerPriceService serverPriceServiceImpl;

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.HOURS)
    public void updateAllServerPrices() throws IOException {
        updateEuPrices();
        updateUsPrices();
    }

    public void updateUsPrices() throws IOException {
        log.info("Updating US prices");
        List<ServerResponse> usServers = serverServiceImpl.getAllServersForRegion(Region.US);

        String usPricesJson = g2GPriceClient.getPrices(Region.US.g2gId);
        List<ServerPriceResponse> usPrices = extractPricesFromJson(usPricesJson);
        List<ServerPrice> pricesToSave = getPricesToSave(usServers, usPrices);
        serverPriceServiceImpl.saveAllPrices(pricesToSave);
        log.info("Finished updating US prices");
    }

    public void updateEuPrices() throws IOException {
        log.info("Updating EU prices");
        List<ServerResponse> euServers = serverServiceImpl.getAllServersForRegion(Region.EU);

        String euPricesJson = g2GPriceClient.getPrices(Region.EU.g2gId);
        String ruPricesJson = g2GPriceClient.getPrices(Region.RU.g2gId);

        List<ServerPriceResponse> euPrices = extractPricesFromJson(euPricesJson);
        List<ServerPriceResponse> ruPrices = extractPricesFromJson(ruPricesJson);
        euPrices.addAll(ruPrices);

        List<ServerPrice> pricesToSave = getPricesToSave(euServers, euPrices);
        serverPriceServiceImpl.saveAllPrices(pricesToSave);
        log.info("Finished updating EU prices");
    }

    private List<ServerPrice> getPricesToSave(List<ServerResponse> euServers, List<ServerPriceResponse> euPrices) {
        List<ServerPrice> pricesToSave = new ArrayList<>();
        euServers.forEach(server -> {
            ServerPrice price = getPriceForServer(server, euPrices);
            pricesToSave.add(price);
        });
        return pricesToSave;
    }

    private ServerPrice getPriceForServer(ServerResponse server, List<ServerPriceResponse> prices) {
        String uniqueServerName = server.uniqueName();
        BigDecimal price = findPriceForUniqueServerName(prices, uniqueServerName);
        Server serverEntity = serverRepository.getReferenceById(server.id());
        return ServerPrice.builder()
                .price(price)
                .server(serverEntity)
                .build();
    }

    private static BigDecimal findPriceForUniqueServerName(List<ServerPriceResponse> prices, String uniqueServerName) {
        return prices.stream().filter(result -> result.serverName().equals(uniqueServerName))
                .findFirst()
                .map(ServerPriceResponse::price)
                .orElseThrow(() -> new G2GPriceNotFoundException("Price not found for server: " + uniqueServerName));
    }

    private List<ServerPriceResponse> extractPricesFromJson(String json) throws IOException {
        G2GPriceListDeserializer deserializer = new G2GPriceListDeserializer();
        ObjectMapper mapper = new ObjectMapper();
        DeserializationContext context = mapper.getDeserializationContext();
        JsonParser parser = mapper.getFactory().createParser(json);

        return deserializer.deserialize(parser, context);
    }
}