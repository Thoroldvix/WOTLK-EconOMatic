package com.thoroldvix.economatic.item;

import com.thoroldvix.economatic.item.dto.ItemPageResponse;
import com.thoroldvix.economatic.item.dto.ItemRequest;
import com.thoroldvix.economatic.item.dto.ItemResponse;
import com.thoroldvix.economatic.shared.dto.SearchRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;


public interface ItemService {


    ItemPageResponse search(@Valid SearchRequest searchRequest, Pageable pageable);

    ItemPageResponse getAll(Pageable pageable);


    ItemResponse getItem(String itemIdentifier);


    ItemResponse addItem(@Valid ItemRequest itemRequest);

    ItemResponse deleteItem(String itemIdentifier);
}
