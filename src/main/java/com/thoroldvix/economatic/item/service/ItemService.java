package com.thoroldvix.economatic.item.service;

import com.thoroldvix.economatic.item.dto.ItemPageResponse;
import com.thoroldvix.economatic.item.dto.ItemRequest;
import com.thoroldvix.economatic.item.dto.ItemResponse;
import com.thoroldvix.economatic.item.dto.ItemSummaryResponse;
import com.thoroldvix.economatic.shared.dto.SearchRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

import static com.thoroldvix.economatic.shared.error.ErrorMessages.PAGEABLE_CANNOT_BE_NULL;

@Validated
public interface ItemService {


    ItemPageResponse search(@NotNull(message = "Search request cannot be null") SearchRequest searchRequest,
                            @NotNull(message = PAGEABLE_CANNOT_BE_NULL) Pageable pageable);

    ItemPageResponse getAll(@NotNull(message = PAGEABLE_CANNOT_BE_NULL) Pageable pageable);


    ItemResponse getItem(@NotEmpty(message = "Item identifier cannot be null or empty") String itemIdentifier);

    ItemSummaryResponse getSummary();

    ItemResponse addItem(@Valid @NotNull(message = "Item request cannot be null") ItemRequest itemRequest);

    ItemResponse deleteItem(@NotEmpty(message = "Item identifier cannot be null or empty") String itemIdentifier);
}