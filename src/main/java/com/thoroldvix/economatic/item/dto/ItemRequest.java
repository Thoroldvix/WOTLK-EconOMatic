package com.thoroldvix.economatic.item.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record ItemRequest(
        @Min(value = 1, message = "Id cannot be less than 1")
        int id,
        @NotEmpty(message = "Item name cannot be null or empty")
        String name,
        @Min(value = 0, message = "Vendor price cannot be less than 0")
        long vendorPrice,
        @NotEmpty(message = "Item type cannot be null or empty")
        String type,
        @NotEmpty(message = "Item slot cannot be null or empty")
        String slot,
        @NotEmpty(message = "Item quality cannot be null or empty")
        String quality
) {

}
