package com.thoroldvix.economatic.error;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String path
) {

}