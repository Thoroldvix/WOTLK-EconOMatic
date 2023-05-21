package com.thoroldvix.g2gcalculator.item.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;

import java.util.List;

@Builder
@JsonDeserialize(using = ItemPriceListDeserializer.class)
public record ItemPriceList(
        String slug,
        List<ItemPrice> items
) {

}
