package com.thoroldvix.pricepal.item;

import com.thoroldvix.pricepal.shared.SearchRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemService {
    List<ItemResponse> search(SearchRequest searchRequest, Pageable pageable);

    List<ItemResponse> getAll(Pageable pageable);

    List<ItemResponse> getAll();

    ItemResponse getItem(String itemIdentifier);

    ItemSummaryResponse getSummary();

    ItemResponse addItem(ItemRequest itemRequest);
}
