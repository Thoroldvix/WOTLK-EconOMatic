package com.thoroldvix.pricepal.item;

import com.thoroldvix.pricepal.shared.RequestDto;
import com.thoroldvix.pricepal.shared.SearchSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.thoroldvix.pricepal.shared.ValidationUtils.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemService {

    private final ItemMapper itemMapper;
    private final SearchSpecification<Item> searchSpecification;
    private final ItemRepository itemRepository;


    public List<ItemResponse> searchItems(RequestDto requestDto, Pageable pageable) {
        Objects.requireNonNull(requestDto,  "RequestDto is required");
        Objects.requireNonNull(pageable);
        Specification<Item> spec = searchSpecification.createSearchSpecification(requestDto.searchCriteria(),
                requestDto.globalOperator());
        List<Item> items = itemRepository.findAll(spec, pageable).getContent();
        validateListNotNullOrEmpty(items, () -> new ItemNotFoundException("Items not found"));
        return itemMapper.toitemResponseList(items);
    }

    public List<ItemResponse> getAllItems(Pageable pageable) {
        Objects.requireNonNull(pageable);
        List<Item> items = itemRepository.findAll(pageable).getContent();
        validateListNotNullOrEmpty(items, () -> new ItemNotFoundException("Items not found"));
        return itemMapper.toitemResponseList(items);
    }

    public long countItems() {
         return itemRepository.count();
    }

    public ItemResponse getItemByItemIdentifier(String itemIdentifier) {
        validateNonNullOrEmptyString(itemIdentifier, "Item identifier cannot be null or empty");
        Optional<Item> item;
        if (isNumber(itemIdentifier)) {
            int itemId = Integer.parseInt(itemIdentifier);
            item = itemRepository.findById(itemId);
            if (item.isEmpty()) {
                throw new ItemNotFoundException("No item found for id " + itemId);
            }
        } else {
            item = itemRepository.findByUniqueName(itemIdentifier);
            if (item.isEmpty()) {
                throw new ItemNotFoundException("No item found for unique name " + itemIdentifier);
            }
        }
        return itemMapper.toItemResponse(item.get());
    }

}