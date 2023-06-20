package com.thoroldvix.pricepal.item;

import com.thoroldvix.pricepal.shared.SearchRequest;
import com.thoroldvix.pricepal.shared.SearchSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.thoroldvix.pricepal.shared.ValidationUtils.validateCollectionNotNullOrEmpty;
import static com.thoroldvix.pricepal.shared.ValidationUtils.validateStringNonNullOrEmpty;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    public static final String ITEMS_NOT_FOUND = "Items not found";
    private final ItemMapper itemMapper;
    private final SearchSpecification<Item> searchSpecification;
    private final ItemRepository itemRepository;

    @Override
    public ItemPagedResponse search(SearchRequest searchRequest, Pageable pageable) {
        Objects.requireNonNull(searchRequest, "SearchRequest cannot be null");
        Objects.requireNonNull(pageable, "Pageable cannot be null");
        Page<Item> items = findAllForSearch(searchRequest, pageable);
        validateCollectionNotNullOrEmpty(items.getContent(), () -> new ItemNotFoundException(ITEMS_NOT_FOUND));
        return itemMapper.toPagedResponse(items);
    }

    @Override
    public ItemPagedResponse getAll(Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable cannot be null");
        Page<Item> page = itemRepository.findAll(pageable);
        validateCollectionNotNullOrEmpty(page.getContent(), () -> new ItemNotFoundException(ITEMS_NOT_FOUND));
        return itemMapper.toPagedResponse(page);
    }

    @Override
    public List<ItemResponse> getAll() {
        List<Item> items = itemRepository.findAll();
        validateCollectionNotNullOrEmpty(items, () -> new ItemNotFoundException(ITEMS_NOT_FOUND));
        return itemMapper.toResponseList(items);
    }

    @Override
    public ItemResponse getItem(String itemIdentifier) {
        validateStringNonNullOrEmpty(itemIdentifier, "Item identifier cannot be null or empty");
        Optional<Item> item = findItem(itemIdentifier);
        return item.map(itemMapper::toResponse)
                .orElseThrow(() -> new ItemNotFoundException("No item found for identifier " + itemIdentifier));
    }


    @Override
    public ItemSummaryResponse getSummary() {
        ItemSummaryProjection summaryProjection = itemRepository.getSummary();
        return itemMapper.toSummaryResponse(summaryProjection);
    }

    @Override
    @Transactional
    public ItemResponse addItem(ItemRequest itemRequest) {
        Objects.requireNonNull(itemRequest, "ItemRequest cannot be null");
        Item item = itemMapper.fromRequest(itemRequest);
        itemRepository.findById(item.getId()).ifPresent(i -> {
            throw new ItemAlreadyExistsException("Item with id " + item.getId() + " already exists");
        });
        return itemMapper.toResponse(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemResponse deleteItem(String itemIdentifier) {
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