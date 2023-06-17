package com.thoroldvix.pricepal.goldprice;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;

@Builder
@JsonDeserialize(using = G2GPriceListDeserializer.class)
public record G2GPriceList() {
}
