package com.thoroldvix.economatic.deal;

public interface ItemDealsService {

    ItemDealsList getDealsForServer(String serverIdentifier, int minQuantity, int minQuality, int limit);
}
