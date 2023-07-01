package com.thoroldvix.economatic.item.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.validation.annotation.Validated;

@Builder
@Validated
public record ItemRequest(
        @Min(1)
        int id,
        @NotBlank(message = "Item itemName cannot be blank")
        @NotNull(message = "Item itemName cannot be null")
        String name,
        @Min(0)
        long vendorPrice,
        @NotBlank(message = "Item type cannot be blank")
        @NotNull(message = "Item type cannot be null")
        String type,
        @NotBlank(message = "Item slot cannot be blank")
        @NotNull(message = "Item slot cannot be null")
        String slot,
        @NotBlank(message = "Item quality cannot be blank")
        @NotNull(message = "Item quality cannot be null")
        String quality
) {

}
