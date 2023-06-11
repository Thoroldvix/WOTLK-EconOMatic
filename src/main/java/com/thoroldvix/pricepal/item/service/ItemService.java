package com.thoroldvix.pricepal.item.service;

import com.thoroldvix.pricepal.item.dto.ItemInfo;
import com.thoroldvix.pricepal.item.entity.Item;
import com.thoroldvix.pricepal.item.repository.ItemRepository;
import com.vaadin.flow.router.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.thoroldvix.pricepal.common.util.ValidationUtils.hasText;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemService {

    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;




    public ItemInfo getItem(String itemName) {
        if (!hasText(itemName))
            throw new IllegalArgumentException("Item name must be valid");

        return itemRepository.findByUniqueName(itemName).map(itemMapper::toItemInfo)
                .orElseThrow(() -> new NotFoundException("Item with name " + itemName + " was not found"));
    }


    public ItemInfo getItem(int itemId) {
        if (itemId <= 0)
            throw new IllegalArgumentException("Item id must be valid");

        return itemRepository.findById(itemId).map(itemMapper::toItemInfo)
                .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " was not found"));
    }

    @Cacheable("item-cache")
    public Set<ItemInfo> getAllItems(Pageable pageable) {
        return itemRepository.findAll(pageable).stream()
                .map(itemMapper::toItemInfo)
                .collect(Collectors.toSet());
    }


    @Cacheable("item-cache")
    public Set<ItemInfo> getAllItems() {
        return itemRepository.findAll().stream()
                .map(itemMapper::toItemInfo)
                .collect(Collectors.toSet());
    }



    @Transactional
    public void saveItem(Item item) {
        Objects.requireNonNull(item, "Item must be valid");
        itemRepository.save(item);
    }


    public long getItemCount() {
        return itemRepository.count();
    }


    @Cacheable("item-cache")
    public Set<ItemInfo> findItemsByIds(List<Integer> ids) {
       if (ids == null || ids.isEmpty()) {
           throw new IllegalArgumentException("Item ids must be valid");
       }
       return itemRepository.findAllById(ids).stream()
                .map(itemMapper::toItemInfo)
                .collect(Collectors.toSet());
    }


    @Cacheable("item-cache")
    public List<ItemInfo> searchItems(String query, Pageable pageable) {
        return itemRepository.searchItemsByName(query, pageable).getContent();
    }




}