package com.thoroldvix.economatic.deal;

import jakarta.validation.Valid;

public interface ItemDealsService {

    ItemDealsList getDealsForServer(@Valid ItemDealsRequest request);
}
