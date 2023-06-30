package com.thoroldvix.economatic.itemprice;

import com.google.common.util.concurrent.RateLimiter;
import com.thoroldvix.economatic.item.Item;
import com.thoroldvix.economatic.item.ItemResponse;
import com.thoroldvix.economatic.item.ItemService;
import com.thoroldvix.economatic.server.Server;
import com.thoroldvix.economatic.server.ServerResponse;
import com.thoroldvix.economatic.server.ServerService;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.thoroldvix.economatic.shared.Utils.elapsedTimeInMillis;


@Service
@Slf4j
public final class ItemPriceUpdateService {
    private static final RateLimiter RATE_LIMITER = RateLimiter.create(4);
    public static final String UPDATE_RATE = "${economatic.item-price.update-rate}";
    public static final String UPDATE_ON_STARTUP_OR_DEFAULT = "#{${economatic.update-on-startup} ? -1 : ${economatic.item-price.update-rate}}";

    private final EntityManager entityManager;
    private final NexusHubClient nexusHubClient;
    private final ItemPriceService itemPriceService;
    private final Set<Integer> itemIds;
    private final Map<String, Integer> serverIdentifiers;

    @Autowired
    public ItemPriceUpdateService(EntityManager entityManager,
                                   NexusHubClient nexusHubClient,
                                   ServerService serverService,
                                   ItemService itemService,
                                   ItemPriceService itemPriceService) {
        this.entityManager = entityManager;
        this.nexusHubClient = nexusHubClient;
        this.itemPriceService = itemPriceService;
        serverIdentifiers = getServerIds(serverService);
        itemIds = getItemIds(itemService);
    }

    private static Set<Integer> getItemIds(ItemService itemService) {
        return itemService.getAll(Pageable.unpaged()).items().stream()
                .map(ItemResponse::id)
                .collect(Collectors.toSet());
    }

    private static Map<String, Integer> getServerIds(ServerService serverService) {
        return serverService.getAll().stream()
                .collect(Collectors.toMap(ServerResponse::uniqueName, ServerResponse::id, (id1, id2) -> id1));
    }

    @Scheduled(fixedRateString = UPDATE_RATE,
            initialDelayString = UPDATE_ON_STARTUP_OR_DEFAULT,
            timeUnit = TimeUnit.HOURS)
    @Retryable(maxAttempts = 5)
    private void update() {
        log.info("Updating item prices");
        Instant start = Instant.now();
        serverIdentifiers.keySet().parallelStream()
                .forEach(serverName -> {
                    List<ItemPrice> itemPrices = retrieveItemPrices(serverName);
                    itemPriceService.saveAll(itemPrices);
                });
        log.info("Finished updating item prices in {} ms", elapsedTimeInMillis(start));
    }


    private List<ItemPrice> retrieveItemPrices(String serverName) {
        RATE_LIMITER.acquire();
        NexusHubResponse nexusHubResponse = nexusHubClient.fetchAllItemPricesForServer(serverName);
        Server server = getServer(nexusHubResponse.slug());
        List<NexusHubResponse.NexusHubPrice> filteredPrices = filterPrices(nexusHubResponse.data());
        return getItemPrices(server, filteredPrices);
    }


    private Server getServer(String uniqueServerName) {
        int serverId = serverIdentifiers.get(uniqueServerName);
        return entityManager.getReference(Server.class, serverId);
    }


    private List<NexusHubResponse.NexusHubPrice> filterPrices(List<NexusHubResponse.NexusHubPrice> prices) {
        return prices.stream()
                .filter(this::filterPrice)
                .toList();
    }


    private boolean filterPrice(NexusHubResponse.NexusHubPrice price) {
        return itemIds.contains(price.itemId()) &&
                price.quantity() > 0 &&
                price.minBuyout() > 0 &&
                price.marketValue() > 0 &&
                price.numAuctions() > 0;
    }


    private List<ItemPrice> getItemPrices(Server server, List<NexusHubResponse.NexusHubPrice> filteredPrices) {
        return filteredPrices.stream()
                .map(itemResponse -> getItemPrice(server, itemResponse))
                .toList();
    }


    private ItemPrice getItemPrice(Server server, NexusHubResponse.NexusHubPrice price) {
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
