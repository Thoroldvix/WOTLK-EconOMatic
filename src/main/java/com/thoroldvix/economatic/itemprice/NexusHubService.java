package com.thoroldvix.economatic.itemprice;

import com.google.common.util.concurrent.RateLimiter;
import com.thoroldvix.economatic.item.ItemResponse;
import com.thoroldvix.economatic.item.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
class NexusHubService {
    private static final RateLimiter RATE_LIMITER = RateLimiter.create(4);
    private final NexusHubClient nexusHubClient;
    private final Set<Integer> itemIds;

    @Autowired
    public NexusHubService(NexusHubClient nexusHubClient, ItemService itemService) {
        this.nexusHubClient = nexusHubClient;
        this.itemIds = getItemIds(itemService);
    }

    public List<NexusHubResponse.NexusHubPrice> retrieveItemPricesForServer(String serverName) {
        RATE_LIMITER.acquire();
        NexusHubResponse nexusHubResponse = nexusHubClient.fetchAllItemPricesForServer(serverName);
        return filterPriceList(nexusHubResponse.data());
    }

    private boolean filterPrice(NexusHubResponse.NexusHubPrice price) {
        return itemIds.contains(price.itemId()) &&
               price.quantity() > 0 &&
               price.minBuyout() > 0 &&
               price.marketValue() > 0 &&
               price.numAuctions() > 0;
    }

    private List<NexusHubResponse.NexusHubPrice> filterPriceList(List<NexusHubResponse.NexusHubPrice> prices) {
        return prices.stream()
                .filter(this::filterPrice)
                .toList();
    }

    private Set<Integer> getItemIds(ItemService itemService) {
        return itemService.getAll(Pageable.unpaged()).items().stream()
                .map(ItemResponse::id)
                .collect(Collectors.toSet());
    }
}
