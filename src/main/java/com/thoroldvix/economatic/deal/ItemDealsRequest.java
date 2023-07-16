package com.thoroldvix.economatic.deal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

public record ItemDealsRequest(
        @NotEmpty(message = "Server identifier cannot be null or empty")
        String serverIdentifier,
        @Min(value = 1, message = "Minimum quantity must be a positive integer")
        int minQuantity,
        @Min(value = 0, message = "Min quality cannot be less than 0")
        int minQuality,
        @Min(value = 1, message = "Limit must be a positive integer")
        int limit
) {

}
