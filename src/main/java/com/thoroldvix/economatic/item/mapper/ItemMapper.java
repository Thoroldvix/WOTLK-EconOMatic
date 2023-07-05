package com.thoroldvix.economatic.item.mapper;


import com.thoroldvix.economatic.item.dto.ItemPageResponse;
import com.thoroldvix.economatic.item.dto.ItemRequest;
import com.thoroldvix.economatic.item.dto.ItemResponse;
import com.thoroldvix.economatic.item.model.Item;
import com.thoroldvix.economatic.item.model.ItemQuality;
import com.thoroldvix.economatic.item.model.ItemSlot;
import com.thoroldvix.economatic.item.model.ItemType;
import com.thoroldvix.economatic.shared.dto.PaginationInfo;
import com.thoroldvix.economatic.shared.util.StringEnumConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemMapper {

    private static <T extends Enum<T>> T getEnumValue(String enumString, Class<T> enumType) {
        return enumString != null ? StringEnumConverter.fromString(enumString, enumType) : null;
    }

    @Mapping(target = "id", source = "id")
    ItemResponse toResponse(Item item);

    @Mapping(target = "type", source = "type", qualifiedByName = "getType")
    @Mapping(target = "quality", source = "quality", qualifiedByName = "getQuality")
    @Mapping(target = "slot", source = "slot", qualifiedByName = "getSlot")
    @Mapping(target = "uniqueName", source = "name")
    Item fromRequest(ItemRequest itemRequest);

    List<ItemResponse> toResponseList(List<Item> items);

    default ItemPageResponse toPageResponse(Page<Item> page) {
        List<ItemResponse> items = toResponseList(page.getContent());
        return new ItemPageResponse(new PaginationInfo(page), items);
    }

    @Named("getType")
    default ItemType getType(String type) {
        return getEnumValue(type, ItemType.class);
    }

    @Named("getQuality")
    default ItemQuality getQuality(String quality) {
        return getEnumValue(quality, ItemQuality.class);
    }

    @Named("getSlot")
    default ItemSlot getSlot(String slot) {
        return getEnumValue(slot, ItemSlot.class);
    }
}

