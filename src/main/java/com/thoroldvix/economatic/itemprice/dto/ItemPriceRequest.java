package com.thoroldvix.economatic.itemprice.dto;


import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import org.springframework.validation.annotation.Validated;

import java.util.Set;

@Builder
@Validated
public record ItemPriceRequest(
        Set<String> serverList,
        @NotEmpty(message = "Item list cannot be null or empty")
        Set<String> itemList
) {}

