package com.thoroldvix.pricepal.goldprice;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoroldvix.pricepal.server.ServerService;
import com.thoroldvix.pricepal.server.ServerResponse;
import com.thoroldvix.pricepal.server.Region;
import com.thoroldvix.pricepal.server.Server;
import com.thoroldvix.pricepal.server.ServerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class GoldPriceUpdateService {

    private final ServerRepository serverRepository;

    private final G2GPriceClient g2GPriceClient;

    private final ServerService serverServiceImpl;

    private final GoldPriceService goldPriceServiceImpl;

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.HOURS)
    public void updateAllServerPrices()  {
        updateEuPrices();
        updateUsPrices();
    }

    public void updateUsPrices()  {
        log.info("Updating US prices");
        List<ServerResponse> usServers = serverServiceImpl.getAllServersForRegion(Region.US);

        String usPricesJson = g2GPriceClient.getPrices(Region.US.g2gId);
        List<GoldPriceResponse> usPrices = extractPricesFromJson(usPricesJson);
        List<GoldPrice> pricesToSave = getPricesToSave(usServers, usPrices);
        goldPriceServiceImpl.saveAllPrices(pricesToSave);
        log.info("Finished updating US prices");
    }

    public void updateEuPrices() {
        log.info("Updating EU prices");
        List<ServerResponse> euServers = serverServiceImpl.getAllServersForRegion(Region.EU);

        String euPricesJson = g2GPriceClient.getPrices(Region.EU.g2gId);
        String ruPricesJson = g2GPriceClient.getPrices(Region.RU.g2gId);

        List<GoldPriceResponse> euPrices = extractPricesFromJson(euPricesJson);
        List<GoldPriceResponse> ruPrices = extractPricesFromJson(ruPricesJson);
        euPrices.addAll(ruPrices);

        List<GoldPrice> pricesToSave = getPricesToSave(euServers, euPrices);
        goldPriceServiceImpl.saveAllPrices(pricesToSave);
        log.info("Finished updating EU prices");
    }

    private List<GoldPrice> getPricesToSave(List<ServerResponse> euServers, List<GoldPriceResponse> euPrices) {
        List<GoldPrice> pricesToSave = new ArrayList<>();
        euServers.forEach(server -> {
            GoldPrice price = getPriceForServer(server, euPrices);
            pricesToSave.add(price);
        });
        return pricesToSave;
    }

    private GoldPrice getPriceForServer(ServerResponse server, List<GoldPriceResponse> prices) {
        String uniqueServerName = server.uniqueName();
        BigDecimal price = findPriceForUniqueServerName(prices, uniqueServerName);
        Server serverEntity = serverRepository.getReferenceById(server.id());
        return GoldPrice.builder()
                .price(price)
                .server(serverEntity)
                .build();
    }

    private static BigDecimal findPriceForUniqueServerName(List<GoldPriceResponse> prices, String uniqueServerName) {
        return prices.stream().filter(result -> result.serverName().equals(uniqueServerName))
                .findFirst()
                .map(GoldPriceResponse::price)
                .orElseThrow(() -> new G2GPriceNotFoundException("Price not found for server: " + uniqueServerName));
    }

    private List<GoldPriceResponse> extractPricesFromJson(String json) {
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