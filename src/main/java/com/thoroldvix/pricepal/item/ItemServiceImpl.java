package com.thoroldvix.pricepal.item;

import com.thoroldvix.pricepal.shared.SearchRequest;
import com.thoroldvix.pricepal.shared.SearchSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.thoroldvix.pricepal.shared.ValidationUtils.*;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemMapper itemMapper;
    private final ItemSummaryMapper itemSummaryMapper;
    private final SearchSpecification<Item> searchSpecification;
    private final ItemRepository itemRepository;

    @Override
    public List<ItemResponse> search(SearchRequest searchRequest, Pageable pageable) {
        Objects.requireNonNull(searchRequest, "SearchRequest cannot be null");
        Objects.requireNonNull(pageable,  "Pageable cannot be null");
        Specification<Item> spec = searchSpecification.createSearchSpecification(searchRequest.globalOperator(),
                searchRequest.searchCriteria());
        List<Item> items = itemRepository.findAll(spec, pageable).getContent();
        validateCollectionNotNullOrEmpty(items, () -> new ItemNotFoundException("Items not found"));
        return itemMapper.toResponseList(items);
    }

    @Override
    public List<ItemResponse> getAll(Pageable pageable) {
        Objects.requireNonNull(pageable);
        List<Item> items = itemRepository.findAll(pageable).getContent();
        validateCollectionNotNullOrEmpty(items, () -> new ItemNotFoundException("Items not found"));
        return itemMapper.toResponseList(items);
    }

    @Override
    public List<ItemResponse> getAll() {
        List<Item> items = itemRepository.findAll();
        validateCollectionNotNullOrEmpty(items, () -> new ItemNotFoundException("Items not found"));
        return itemMapper.toResponseList(items);
    }

    @Override
    public ItemResponse getItem(String itemIdentifier) {
        validateStringNonNullOrEmpty(itemIdentifier, "Item identifier cannot be null or empty");
        Optional<Item> item;
        if (isNumber(itemIdentifier)) {
            int itemId = Integer.parseInt(itemIdentifier);
            item = itemRepository.findById(itemId);
        } else {
            item = itemRepository.findByUniqueName(itemIdentifier);
        }
        return item.map(itemMapper::toResponse).orElseThrow(() -> new ItemNotFoundException("No item found for identifier " + itemIdentifier));
    }

    @Override
    public ItemSummaryResponse getSummary() {
        ItemSummaryProjection summaryProjection = itemRepository.getSummary();
        return itemSummaryMapper.toResponse(summaryProjection);
    }

    @Override
    @Transactional
    public ItemResponse addItem(ItemRequest itemRequest) {
        Objects.requireNonNull(itemRequest, "ItemRequest cannot be null");
        Item item = itemMapper.fromRequest(itemRequest);
        return itemMapper.toResponse(itemRepository.save(item));
    }
}