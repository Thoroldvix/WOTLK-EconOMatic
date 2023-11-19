package com.thoroldvix.economatic.item;

import com.thoroldvix.economatic.search.SearchRequest;
import com.thoroldvix.economatic.search.SpecificationBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.thoroldvix.economatic.common.util.ValidationUtils.notEmpty;
import static com.thoroldvix.economatic.error.ErrorMessages.ITEM_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY;

@Service
@Cacheable("item-cache")
@Transactional(readOnly = true)
@RequiredArgsConstructor
class ItemServiceImpl implements ItemService {

    public static final String ITEMS_NOT_FOUND = "Items not found";
    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;

    @Override
    public ItemPageResponse search(@Valid SearchRequest searchRequest, Pageable pageable) {
        Specification<Item> spec = SpecificationBuilder.from(searchRequest);
        Page<Item> items = itemRepository.findAll(spec, pageable);
        notEmpty(items.getContent(), () -> new ItemNotFoundException(ITEMS_NOT_FOUND));

        return itemMapper.toPageResponse(items);
    }

    @Override
    public ItemPageResponse getAll(Pageable pageable) {
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
        Item item = itemMapper.fromRequest(itemRequest);
        itemRepository.findById(item.getId()).ifPresent(i -> {
            throw new ItemAlreadyExistsException("Item with id " + item.getId() + " already exists");
        });

        return itemMapper.toResponse(itemRepository.save(item));
    }

}
