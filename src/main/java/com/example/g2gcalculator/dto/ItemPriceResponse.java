package com.example.g2gcalculator.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ItemPriceResponse(
        BigDecimal price

) {

}