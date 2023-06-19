package com.thoroldvix.pricepal.item;


import com.thoroldvix.pricepal.shared.StringEnumConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemMapper {

    @Mapping(target = "itemId", source = "id")
    ItemResponse toResponse(Item item);

    @Mapping(target = "type", source = "type", qualifiedByName = "getType")
    @Mapping(target = "quality", source = "quality", qualifiedByName = "getQuality")
    @Mapping(target = "slot", source = "slot", qualifiedByName = "getSlot")
    Item fromRequest(ItemRequest itemRequest);

    List<ItemResponse> toResponseList(List<Item> items);

    @Named("getType")
    default ItemType getType(String type) {
        return StringEnumConverter.fromString(type, ItemType.class);
    }
    @Named("getQuality")
    default ItemQuality  getQuality(String quality) {
        return StringEnumConverter.fromString(quality, ItemQuality.class);
    }
    @Named("getSlot")
    default ItemSlot getSlot(String slot) {
        return StringEnumConverter.fromString(slot, ItemSlot.class);
    }

}