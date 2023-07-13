package com.thoroldvix.economatic.goldprice;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.util.Set;

@Builder
public record GoldPriceRequest(
        @NotEmpty(message = "Server list cannot be null or empty")
        Set<String> serverList
) {
}
