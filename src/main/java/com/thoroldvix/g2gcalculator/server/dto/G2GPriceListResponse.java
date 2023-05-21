package com.thoroldvix.g2gcalculator.server.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize(using = G2GPriceListDeserializer.class)
public record G2GPriceListResponse(
        List<ServerPrice> prices
) {

}