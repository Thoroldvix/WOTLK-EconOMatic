package com.thoroldvix.economatic.goldprice;

import com.thoroldvix.economatic.server.Server;
import com.thoroldvix.economatic.server.ServerResponse;
import com.thoroldvix.economatic.server.ServerService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.thoroldvix.economatic.common.util.Utils.elapsedTimeInMillis;

@Service
@RequiredArgsConstructor
@Slf4j
class GoldPriceUpdateService {

    public static final String UPDATE_ON_STARTUP_OR_DEFAULT = "#{${economatic.update-on-startup} ? -1 : ${economatic.gold-price.update-rate}}";
    public static final String UPDATE_RATE = "${economatic.gold-price.update-rate}";

    @PersistenceContext
    private final EntityManager entityManager;
    private final GoldPriceServiceImpl goldPriceServiceImpl;
    private final G2GService g2gService;
    private final ServerService serverServiceImpl;

    @Scheduled(fixedRateString = UPDATE_RATE,
            initialDelayString = UPDATE_ON_STARTUP_OR_DEFAULT,
            timeUnit = TimeUnit.MINUTES)
    @Retryable(maxAttempts = 5)
    protected void update() {
        log.info("Updating gold prices");
        Instant start = Instant.now();

        List<GoldPriceResponse> prices = g2gService.getGoldPrices();
        List<ServerResponse> servers = serverServiceImpl.getAll().servers();
        List<GoldPrice> pricesToSave = getPriceList(servers, prices);

        goldPriceServiceImpl.saveAll(pricesToSave);
        log.info("Finished updating gold prices in {} ms", elapsedTimeInMillis(start));
    }

    private List<GoldPrice> getPriceList(List<ServerResponse> servers, List<GoldPriceResponse> prices) {
        return servers.stream()
                .map(server -> getPriceForServer(server, prices))
                .toList();
    }

    private GoldPrice getPriceForServer(ServerResponse server, List<GoldPriceResponse> prices) {
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
        Server serverEntity = entityManager.getReference(Server.class, server.id());
        return GoldPrice.builder()
                .value(price)
                .server(serverEntity)
                .build();
    }
}