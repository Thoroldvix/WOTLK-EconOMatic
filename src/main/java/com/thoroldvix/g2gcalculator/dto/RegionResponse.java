package com.thoroldvix.g2gcalculator.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record RegionResponse(
        UUID g2gRegionId,
        String name
) {

}