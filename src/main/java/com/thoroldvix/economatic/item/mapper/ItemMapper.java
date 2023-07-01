package com.thoroldvix.economatic.item.mapper;


import com.thoroldvix.economatic.item.dto.ItemPagedResponse;
import com.thoroldvix.economatic.item.dto.ItemRequest;
import com.thoroldvix.economatic.item.dto.ItemResponse;
import com.thoroldvix.economatic.item.model.Item;
import com.thoroldvix.economatic.item.model.ItemQuality;
import com.thoroldvix.economatic.item.model.ItemSlot;
import com.thoroldvix.economatic.item.model.ItemType;
import com.thoroldvix.economatic.shared.util.StringEnumConverter;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.thoroldvix.economatic.shared.error.ErrorMessages.PAGE_CANNOT_BE_NULL;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
@Validated
public interface ItemMapper {

    @Mapping(target = "id", source = "id")
    ItemResponse toResponse(
            Item item);

    @Mapping(target = "type", source = "type", qualifiedByName = "getType")
    @Mapping(target = "quality", source = "quality", qualifiedByName = "getQuality")
    @Mapping(target = "slot", source = "slot", qualifiedByName = "getSlot")
    Item fromRequest(
            ItemRequest itemRequest);

    List<ItemResponse> toResponseList(
            @NotEmpty(message = "Items list cannot be null or empty")
            List<Item> items);


    default ItemPagedResponse toPagedResponse(
            @NotNull(message = PAGE_CANNOT_BE_NULL)
            Page<Item> items) {

        return ItemPagedResponse.builder()
                .items(toResponseList(items.getContent()))
                .totalPages(items.getTotalPages())
                .totalElements(items.getTotalElements())
                .page(items.getNumber())
                .pageSize(items.getSize())
                .build();
    }

    @Named("getType")
    default ItemType getType(
            @NotEmpty(message = "Type cannot be null or empty")
            String type) {
        return StringEnumConverter.fromString(type, ItemType.class);
    }

    @Named("getQuality")
    default ItemQuality getQuality(
            @NotEmpty(message = "Quality cannot be null or empty")
            String quality) {
        return StringEnumConverter.fromString(quality, ItemQuality.class);
    }

    @Named("getSlot")
    default ItemSlot getSlot(
            @NotEmpty(message = "Slot cannot be null or empty")
            String slot) {
        return StringEnumConverter.fromString(slot, ItemSlot.class);
    }
}

