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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemPriceUpdateService {
    @PersistenceContext
    private final EntityManager entityManager;
    private final NexusHubClient nexusHubClient;
    private final ItemPriceService itemPriceService;
    private final Set<Integer> ITEM_IDS;
    private final Map<String, Integer> SERVER_IDENTIFIERS;
    private final RateLimiter rateLimiter = RateLimiter.create(4);


    public ItemPriceUpdateService(EntityManager entityManager,
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

    @Scheduled(fixedRate = 3, timeUnit = TimeUnit.HOURS)
    protected void updateItemPrices() {
        log.info("Updating item prices");
        Instant start = Instant.now();
        SERVER_IDENTIFIERS.keySet().parallelStream()
                .forEach(serverName -> {
                    List<ItemPrice> itemPrices = retrieveItemPrices(serverName);
                    itemPriceService.saveAll(itemPrices);
                });
        Instant finish = Instant.now();
        log.info("Finished updating item prices in {} ms", Duration.between(start, finish).toSeconds());
    }

    private List<ItemPrice> retrieveItemPrices(String serverName) {
        rateLimiter.acquire();
        AuctionHouseInfo auctionHouseInfo = nexusHubClient.getAllItemPricesForServer(serverName);
        Server server = getServer(SERVER_IDENTIFIERS, auctionHouseInfo.slug());
        return getItemPrices(auctionHouseInfo.items(), server, ITEM_IDS);
    }

    private Set<Integer> getItemIds(ItemService itemService) {
        return itemService.getAll().stream()
                .map(ItemResponse::id)
                .collect(Collectors.toSet());
    }

    private Server getServer(Map<String, Integer> serverIds, String uniqueServerName) {
        return entityManager.getReference(Server.class, (serverIds.get(uniqueServerName)));
    }

    private Map<String, Integer> getServerIds(ServerService serverService) {
        return serverService.getAll().stream()
                .collect(Collectors.toMap(ServerResponse::uniqueName, ServerResponse::id, (id1, id2) -> id1));
    }

    private List<ItemPrice> getItemPrices(List<ItemPriceResponse> items, Server server, Set<Integer> itemIds) {
        return items.stream()
                .filter(itemPriceResponse -> itemIds.contains(itemPriceResponse.itemId()))
                .map(itemResponse -> getItemPrice(server, itemResponse))
                .collect(Collectors.toList());
    }

    private ItemPrice getItemPrice(Server server, ItemPriceResponse itemResponse) {
        Item item = entityManager.getReference(Item.class, itemResponse.itemId());
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
