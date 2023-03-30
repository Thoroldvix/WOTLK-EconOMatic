package com.thoroldvix.g2gcalculator.g2g;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.thoroldvix.g2gcalculator.price.PriceResponse;

import java.math.BigDecimal;
import java.util.List;

@JsonDeserialize(using = G2GPriceDeserializer.class)
public record G2GPriceResponse(
        List<PriceResponse> prices
) {

}