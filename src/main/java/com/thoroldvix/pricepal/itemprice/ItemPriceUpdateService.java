package com.thoroldvix.pricepal.itemprice;

import com.google.common.util.concurrent.RateLimiter;
import com.thoroldvix.pricepal.item.Item;
import com.thoroldvix.pricepal.item.ItemResponse;
import com.thoroldvix.pricepal.item.ItemService;
import com.thoroldvix.pricepal.server.Server;
import com.thoroldvix.pricepal.server.ServerResponse;
import com.thoroldvix.pricepal.server.ServerService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public final class ItemPriceUpdateService {
    private static final RateLimiter RATE_LIMITER = RateLimiter.create(4);
    @PersistenceContext
    private final EntityManager entityManager;
    private final NexusHubClient nexusHubClient;
    private final ItemPriceService itemPriceService;
    private final Set<Integer> itemIds;
    private final Map<String, Integer> serverIdentifiers;


    private ItemPriceUpdateService(EntityManager entityManager,
                                   NexusHubClient nexusHubClient,
                                   ServerService serverServiceImpl,
                                   ItemService itemServiceImpl,
                                   ItemPriceService itemPriceService) {
        this.entityManager = entityManager;
        this.nexusHubClient = nexusHubClient;
        this.itemPriceService = itemPriceService;
        serverIdentifiers = getServerIds(serverServiceImpl);
        itemIds = getItemIds(itemServiceImpl);
    }

    private static Set<Integer> getItemIds(ItemService itemService) {
        return itemService.getAll().stream()
                .map(ItemResponse::id)
                .collect(Collectors.toSet());
    }

    private static Map<String, Integer> getServerIds(ServerService serverService) {
        return serverService.getAll().stream()
                .collect(Collectors.toMap(ServerResponse::uniqueName, ServerResponse::id, (id1, id2) -> id1));
    }

    //    todo add retry on fail
//    @Scheduled(fixedRate = 3, timeUnit = TimeUnit.HOURS)
    private void update() {
        log.info("Updating item prices");
        Instant start = Instant.now();
        serverIdentifiers.keySet().parallelStream()
                .forEach(serverName -> {
                    List<ItemPrice> itemPrices = retrieveItemPrices(serverName);
                    itemPriceService.saveAll(itemPrices);
                });
        Instant finish = Instant.now();
        log.info("Finished updating item prices in {} ms", Duration.between(start, finish).toMillis());
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
