package com.thoroldvix.pricepal.item;

import com.thoroldvix.pricepal.shared.RequestDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemService {
    List<ItemResponse> search(RequestDto requestDto, Pageable pageable);

    List<ItemResponse> getAll(Pageable pageable);

    List<ItemResponse> getAll();

    ItemResponse getItem(String itemIdentifier);

    ItemSummaryResponse getSummary();

    ItemResponse addItem(ItemRequest itemRequest);
}
