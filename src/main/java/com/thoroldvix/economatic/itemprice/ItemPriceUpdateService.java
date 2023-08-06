package com.thoroldvix.economatic.itemprice;

import com.thoroldvix.economatic.item.Item;
import com.thoroldvix.economatic.server.Server;
import com.thoroldvix.economatic.server.ServerResponse;
import com.thoroldvix.economatic.server.ServerService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.thoroldvix.economatic.common.util.Utils.elapsedTimeInMillis;

@Service
@Slf4j
class ItemPriceUpdateService {

    public static final String UPDATE_RATE = "${economatic.item-price.update-rate}";
    public static final String UPDATE_ON_STARTUP_OR_DEFAULT = "#{${economatic.update-on-startup} ? -1 : ${economatic.item-price.update-rate}}";

    @PersistenceContext
    private final EntityManager entityManager;
    private final ItemPriceService itemPriceService;
    private final NexusHubService nexusHubService;
    private final Map<String, Integer> serverIdentifiers;

    @Autowired
    public ItemPriceUpdateService(EntityManager entityManager,
                                  ItemPriceService itemPriceService,
                                  NexusHubService nexusHubService,
                                  ServerService serverService) {
        this.entityManager = entityManager;
        this.itemPriceService = itemPriceService;
        this.nexusHubService = nexusHubService;
        this.serverIdentifiers = getServerIds(serverService);
    }

    private Map<String, Integer> getServerIds(ServerService serverService) {
        return serverService.getAll().servers().stream()
                .collect(Collectors.toMap(ServerResponse::uniqueName, ServerResponse::id, (id1, id2) -> id1));
    }

    @Scheduled(fixedRateString = UPDATE_RATE,
            initialDelayString = UPDATE_ON_STARTUP_OR_DEFAULT,
            timeUnit = TimeUnit.HOURS)
    @Retryable(maxAttempts = 5)
    protected void update() {
        log.info("Updating item prices");
        Instant start = Instant.now();

        serverIdentifiers.keySet().parallelStream()
                .forEach(serverName -> {
                    List<ItemPrice> itemPrices = getItemPricesForServer(serverName);
                    itemPriceService.saveAll(itemPrices);
                });

        log.info("Finished updating item prices in {} ms", elapsedTimeInMillis(start));
    }

    private List<ItemPrice> getItemPricesForServer(String serverName) {
        List<NexusHubResponse.NexusHubPrice> nexusHubPrices = nexusHubService.getItemPricesForServer(serverName);
        Server server = getServer(serverName);
        return toItemPriceList(server, nexusHubPrices);
    }

    private Server getServer(String uniqueServerName) {
        int serverId = serverIdentifiers.get(uniqueServerName);
        return entityManager.getReference(Server.class, serverId);
    }

    private List<ItemPrice> toItemPriceList(Server server, List<NexusHubResponse.NexusHubPrice> filteredPrices) {
        return filteredPrices.parallelStream()
                .map(itemResponse -> buildItemPrice(server, itemResponse))
                .toList();
    }

    private ItemPrice buildItemPrice(Server server, NexusHubResponse.NexusHubPrice price) {
        Item item = entityManager.getReference(Item.class, price.itemId());

        return ItemPrice.builder()
                .item(item)
                .marketValue(price.marketValue())
                .historicalValue(price.historicalValue())
                .numAuctions(price.numAuctions())
                .quantity(price.quantity())
                .minBuyout(price.minBuyout())
                .server(server)
                .build();
    }
}
