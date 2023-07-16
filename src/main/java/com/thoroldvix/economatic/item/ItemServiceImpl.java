package com.thoroldvix.economatic.item;

import com.thoroldvix.economatic.search.SearchRequest;
import com.thoroldvix.economatic.search.SpecificationBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.thoroldvix.economatic.common.util.ValidationUtils.notEmpty;
import static com.thoroldvix.economatic.error.ErrorMessages.*;
import static java.util.Objects.requireNonNull;

@Service
@Cacheable("item-cache")
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
class ItemServiceImpl implements ItemService {

    public static final String ITEMS_NOT_FOUND = "Items not found";
    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;

    @Override
    public ItemPageResponse search(@Valid SearchRequest searchRequest, Pageable pageable) {
        requireNonNull(searchRequest, SEARCH_REQUEST_CANNOT_BE_NULL.message);
        requireNonNull(pageable, PAGEABLE_CANNOT_BE_NULL.message);

        Page<Item> items = findAllForSearch(searchRequest, pageable);
        notEmpty(items.getContent(), () -> new ItemNotFoundException(ITEMS_NOT_FOUND));

        return itemMapper.toPageResponse(items);
    }

    private Page<Item> findAllForSearch(SearchRequest searchRequest, Pageable pageable) {
        Specification<Item> spec = SpecificationBuilder.from(searchRequest);
        return itemRepository.findAll(spec, pageable);
    }

    @Override
    public ItemPageResponse getAll(Pageable pageable) {
        requireNonNull(pageable, PAGEABLE_CANNOT_BE_NULL.message);

        Page<Item> page = itemRepository.findAll(pageable);
        notEmpty(page.getContent(), () -> new ItemNotFoundException(ITEMS_NOT_FOUND));

        return itemMapper.toPageResponse(page);
    }

    @Override
    public ItemResponse getItem(String itemIdentifier) {
        notEmpty(itemIdentifier, ITEM_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY.message);

        Optional<Item> item = findItem(itemIdentifier);
        return item.map(itemMapper::toResponse)
                .orElseThrow(() -> new ItemNotFoundException("No item found for identifier " + itemIdentifier));
    }

    private Optional<Item> findItem(String itemIdentifier) {
        try {
            int itemId = Integer.parseInt(itemIdentifier);
            return itemRepository.findById(itemId);
        } catch (NumberFormatException ignored) {
            return itemRepository.findByUniqueName(itemIdentifier);
        }
    }

    @Override
    @Transactional
    public ItemResponse deleteItem(String itemIdentifier) {
        notEmpty(itemIdentifier, ITEM_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY.message);

        Item item = findItem(itemIdentifier)
                .orElseThrow(() -> new ItemDoesNotExistException("No item exists with identifier " + itemIdentifier));
        itemRepository.delete(item);

        return itemMapper.toResponse(item);
    }

    @Override
    @Transactional
    public ItemResponse addItem(@Valid ItemRequest itemRequest) {
        requireNonNull(itemRequest, "Item request cannot be null");

        Item item = itemMapper.fromRequest(itemRequest);
        itemRepository.findById(item.getId()).ifPresent(i -> {
            throw new ItemAlreadyExistsException("Item with id " + item.getId() + " already exists");
        });

        return itemMapper.toResponse(itemRepository.save(item));
    }

}