package com.thoroldvix.g2gcalculator.item.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;

import java.util.List;

@Builder
@JsonDeserialize(using = ItemInfoListDeserializer.class)
public record ItemInfoList(
        String slug,

        List<ItemInfo> items
) {

}
