package com.thoroldvix.economatic.item.service;

import com.thoroldvix.economatic.item.dto.ItemPagedResponse;
import com.thoroldvix.economatic.item.dto.ItemRequest;
import com.thoroldvix.economatic.item.dto.ItemResponse;
import com.thoroldvix.economatic.item.dto.ItemSummaryResponse;
import com.thoroldvix.economatic.item.error.ItemAlreadyExistsException;
import com.thoroldvix.economatic.item.error.ItemDoesNotExistException;
import com.thoroldvix.economatic.item.error.ItemNotFoundException;
import com.thoroldvix.economatic.item.mapper.ItemMapper;
import com.thoroldvix.economatic.item.mapper.ItemSummaryMapper;
import com.thoroldvix.economatic.item.model.Item;
import com.thoroldvix.economatic.item.repository.ItemRepository;
import com.thoroldvix.economatic.item.repository.ItemSummaryProjection;
import com.thoroldvix.economatic.shared.dto.SearchRequest;
import com.thoroldvix.economatic.shared.service.SearchSpecification;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

import static com.thoroldvix.economatic.shared.error.ErrorMessages.PAGEABLE_CANNOT_BE_NULL;
import static com.thoroldvix.economatic.shared.util.Utils.validateCollectionNotEmpty;

@Service
@Validated
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    public static final String ITEMS_NOT_FOUND = "Items not found";
    private final ItemMapper itemMapper;
    private final ItemSummaryMapper itemSummaryMapper;
    private final SearchSpecification<Item> searchSpecification;
    private final ItemRepository itemRepository;


    @Override
    public ItemPagedResponse search(@NotNull(message = "Search request cannot be null") SearchRequest searchRequest,
                                    @NotNull(message = PAGEABLE_CANNOT_BE_NULL) Pageable pageable) {
        Page<Item> items = findAllForSearch(searchRequest, pageable);
        validateCollectionNotEmpty(items.getContent(), () -> new ItemNotFoundException(ITEMS_NOT_FOUND));
        return itemMapper.toPagedResponse(items);
    }

    @Override
    @Cacheable("item-cache")
    public ItemPagedResponse getAll(@NotNull(message = PAGEABLE_CANNOT_BE_NULL) Pageable pageable) {
        Page<Item> page = itemRepository.findAll(pageable);
        validateCollectionNotEmpty(page.getContent(), () -> new ItemNotFoundException(ITEMS_NOT_FOUND));
        return itemMapper.toPagedResponse(page);
    }


    @Override
    @Cacheable("item-cache")
    public ItemResponse getItem(@NotEmpty(message = "Item identifier cannot be null or empty") String itemIdentifier) {
        Optional<Item> item = findItem(itemIdentifier);
        return item.map(itemMapper::toResponse)
                .orElseThrow(() -> new ItemNotFoundException("No item found for identifier " + itemIdentifier));
    }

    @Override
    public ItemSummaryResponse getSummary() {
        ItemSummaryProjection summaryProjection = itemRepository.getSummary();
        return itemSummaryMapper.toSummaryResponse(summaryProjection);
    }

    @Override
    @Transactional
    public ItemResponse addItem(@Valid @NotNull(message = "Item request cannot be null") ItemRequest itemRequest) {
        Item item = itemMapper.fromRequest(itemRequest);
        itemRepository.findById(item.getId()).ifPresent(i -> {
            throw new ItemAlreadyExistsException("Item with id " + item.getId() + " already exists");
        });
        return itemMapper.toResponse(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemResponse deleteItem(@NotEmpty(message = "Item identifier cannot be null or empty") String itemIdentifier) {
        Item item = findItem(itemIdentifier)
                .orElseThrow(() -> new ItemDoesNotExistException("No item exists with identifier " + itemIdentifier));
        itemRepository.delete(item);
        return itemMapper.toResponse(item);
    }

    private Page<Item> findAllForSearch(SearchRequest searchRequest, Pageable pageable) {
        Specification<Item> spec = searchSpecification.create(searchRequest.globalOperator(),
                searchRequest.searchCriteria());
        return itemRepository.findAll(spec, pageable);
    }

    private Optional<Item> findItem(String itemIdentifier) {
        Optional<Item> item;
        try {
            int itemId = Integer.parseInt(itemIdentifier);
            item = itemRepository.findById(itemId);
        } catch (NumberFormatException e) {
            item = itemRepository.findByUniqueName(itemIdentifier);
        }
        return item;
    }

}