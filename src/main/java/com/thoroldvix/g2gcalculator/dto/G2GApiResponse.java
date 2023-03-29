package com.thoroldvix.g2gcalculator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public record G2GApiResponse(
        Payload payload

) {
    public record Payload(
            List<G2GPriceResponse> results
    ) {
        public record G2GPriceResponse(
                @JsonProperty("display_price")
                BigDecimal price,
                @JsonProperty("title")
                String realmName
        ) {
        }
    }
}