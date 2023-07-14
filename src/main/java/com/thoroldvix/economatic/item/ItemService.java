package com.thoroldvix.economatic.item;

import com.thoroldvix.economatic.search.SearchRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

@Validated
public interface ItemService {

    ItemPageResponse search(@Valid SearchRequest searchRequest, Pageable pageable);

    ItemPageResponse getAll(Pageable pageable);

    ItemResponse getItem(String itemIdentifier);

    ItemResponse addItem(@Valid ItemRequest itemRequest);

    ItemResponse deleteItem(String itemIdentifier);
}
