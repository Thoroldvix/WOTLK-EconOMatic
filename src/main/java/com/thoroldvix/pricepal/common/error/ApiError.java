package com.thoroldvix.pricepal.common.error;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String path
) {

}