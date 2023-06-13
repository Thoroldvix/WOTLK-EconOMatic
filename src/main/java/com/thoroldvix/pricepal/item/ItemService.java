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

import static com.thoroldvix.pricepal.shared.ValidationUtils.validateListNotNullOrEmpty;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemService {

    private final ItemMapper itemMapper;
    private final SearchSpecification<Item> searchSpecification;
    private final ItemRepository itemRepository;


    public List<ItemInfo> searchItems(RequestDto requestDto, Pageable pageable) {
        Objects.requireNonNull(requestDto,  "RequestDto is required");
        Objects.requireNonNull(pageable);
        Specification<Item> spec = searchSpecification.createSearchSpecification(requestDto.searchCriteria(),
                requestDto.globalOperator());
        List<Item> items = itemRepository.findAll(spec, pageable).getContent();
        validateListNotNullOrEmpty(items, () -> new ItemNotFoundException("Items not found"));
        return itemMapper.toitemInfoList(items);
    }

    public List<ItemInfo> getAllItems(Pageable pageable) {
        Objects.requireNonNull(pageable);
        List<Item> items = itemRepository.findAll(pageable).getContent();
        validateListNotNullOrEmpty(items, () -> new ItemNotFoundException("Items not found"));
        return itemMapper.toitemInfoList(items);
    }

    public long countItems() {
         return itemRepository.count();
    }
}