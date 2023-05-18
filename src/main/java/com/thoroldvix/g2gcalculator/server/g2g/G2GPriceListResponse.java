package com.thoroldvix.g2gcalculator.server.g2g;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.thoroldvix.g2gcalculator.price.PriceResponse;

import java.util.List;

@JsonDeserialize(using = G2GPriceListDeserializer.class)
public record G2GPriceListResponse(
        List<PriceResponse> prices
) {

}