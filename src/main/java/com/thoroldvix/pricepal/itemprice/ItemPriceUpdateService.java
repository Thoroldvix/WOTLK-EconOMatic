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
    @PersistenceContext
    private final EntityManager entityManager;
    private final NexusHubClient nexusHubClient;
    private final ItemPriceService itemPriceService;
    private final Set<Integer> ITEM_IDS;
    private final Map<String, Integer> SERVER_IDENTIFIERS;
    private final RateLimiter rateLimiter = RateLimiter.create(4);


    private ItemPriceUpdateService(EntityManager entityManager,
                                  NexusHubClient nexusHubClient,
                                  ServerService serverServiceImpl,
                                  ItemService itemServiceImpl,
                                  ItemPriceService itemPriceService) {
        this.entityManager = entityManager;
        this.nexusHubClient = nexusHubClient;
        this.itemPriceService = itemPriceService;
        this.SERVER_IDENTIFIERS = getServerIds(serverServiceImpl);
        this.ITEM_IDS = getItemIds(itemServiceImpl);
    }

//    @Scheduled(fixedRate = 3, timeUnit = TimeUnit.HOURS)
    private void update() {
        log.info("Updating item prices");
        Instant start = Instant.now();
        SERVER_IDENTIFIERS.keySet().parallelStream()
                .forEach(serverName -> {
                    List<ItemPrice> itemPrices = retrieveItemPrices(serverName);
                    itemPriceService.saveAll(itemPrices);
                });
        Instant finish = Instant.now();
        log.info("Finished updating item prices in {} ms", Duration.between(start, finish).toMillis());
    }

    private List<ItemPrice> retrieveItemPrices(String serverName) {
        rateLimiter.acquire();
        AuctionHouseInfo auctionHouseInfo = nexusHubClient.fetchAllItemPricesForServer(serverName);
        Server server = getServer(auctionHouseInfo.server());
        List<ItemPriceResponse> filteredPrices = filterPrices(auctionHouseInfo.items());
        return getItemPrices(server, filteredPrices);
    }


    private Set<Integer> getItemIds(ItemService itemService) {
        return itemService.getAll().stream()
                .map(ItemResponse::itemId)
                .collect(Collectors.toSet());
    }

    private Server getServer(String uniqueServerName) {
        int serverId = SERVER_IDENTIFIERS.get(uniqueServerName);
        return entityManager.getReference(Server.class, serverId);
    }

    private Map<String, Integer> getServerIds(ServerService serverService) {
        return serverService.getAll().stream()
                .collect(Collectors.toMap(ServerResponse::uniqueName, ServerResponse::id, (id1, id2) -> id1));
    }

    private List<ItemPriceResponse> filterPrices(List<ItemPriceResponse> items) {
        return items.stream()
                .filter(itemPriceResponse -> ITEM_IDS.contains(itemPriceResponse.itemInfo().itemId()))
                .filter(itemPriceResponse -> itemPriceResponse.quantity() > 0)
                .filter(itemPriceResponse -> itemPriceResponse.numAuctions() > 0)
                .collect(Collectors.toList());
    }

    private List<ItemPrice> getItemPrices(Server server, List<ItemPriceResponse> filteredPrices) {
        return filteredPrices.stream()
                .map(itemResponse -> getItemPrice(server, itemResponse))
                .toList();
    }

    private ItemPrice getItemPrice(Server server, ItemPriceResponse itemResponse) {
        Item item = entityManager.getReference(Item.class, itemResponse.itemInfo().itemId());
        return ItemPrice.builder()
                .item(item)
                .marketValue(itemResponse.marketValue())
                .historicalValue(itemResponse.historicalValue())
                .numAuctions(itemResponse.numAuctions())
                .quantity(itemResponse.quantity())
                .minBuyout(itemResponse.minBuyout())
                .server(server)
                .build();
    }
}
