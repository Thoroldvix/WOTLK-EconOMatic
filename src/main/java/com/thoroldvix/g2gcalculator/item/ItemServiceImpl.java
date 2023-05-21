package com.thoroldvix.g2gcalculator.item;

import com.thoroldvix.g2gcalculator.common.StringFormatter;
import com.thoroldvix.g2gcalculator.item.dto.ItemInfo;
import com.vaadin.flow.router.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j

public class ItemServiceImpl implements ItemService {

    private final ItemMapper  itemMapper;
    private final ItemRepository itemRepository;


    @Autowired
    public ItemServiceImpl(ItemMapper itemMapper, ItemRepository itemRepository) {
        this.itemMapper = itemMapper;
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemInfo getItemByName(String itemName) {
        if (!StringUtils.hasText(itemName))
            throw new IllegalArgumentException("Item name must be valid");
        String formattedItemName = formatItemName(itemName);
        return itemRepository.findByName(formattedItemName).map(itemMapper::toItemInfo)
                .orElseThrow(() -> new NotFoundException("Item with name " + itemName + " not found"));
    }

    @Override
    public ItemInfo getItemById(int itemId) {
        if (itemId <= 0)
            throw new IllegalArgumentException("Item id must be valid");

        return itemRepository.findById(itemId).map(itemMapper::toItemInfo)
                .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not found"));
    }
    @Override
    public Set<ItemInfo> getAllItems(Pageable pageable) {
        return itemRepository.findAll(pageable).getContent().stream()
                .map(itemMapper::toItemInfo)
                .collect(Collectors.toSet());
    }


    @Override
    @Transactional
    public void saveItem(Item item) {
        Objects.requireNonNull(item, "Item must be valid");
        itemRepository.save(item);
    }

    @Override
    public long getItemCount() {
        return itemRepository.count();
    }

    @Override
    @Cacheable("item-cache")
    public List<ItemInfo> findItemsByIds(List<Integer> ids) {
       if (ids == null || ids.isEmpty()) {
           throw new IllegalArgumentException("Item ids must be valid");
       }

        return itemRepository.findAllById(ids).stream()
                .map(itemMapper::toItemInfo)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable("item-cache")
    public List<ItemInfo> searchItems(String query, Pageable pageable) {
        return itemRepository.searchItemsByName(query, pageable).getContent();
    }


    private String formatItemName(String itemName) {
        return StringFormatter.formatString(itemName, String::toLowerCase);
    }

}