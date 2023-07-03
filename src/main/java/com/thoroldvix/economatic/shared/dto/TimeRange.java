package com.thoroldvix.economatic.shared.dto;

import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;


public record TimeRange(
        LocalDateTime start,
        LocalDateTime end
) {
    public TimeRange(@Min(value = 1, message = "Time range should be at least 1 day") int days) {
        this(LocalDateTime.now().minusDays(days), LocalDateTime.now());
    }
}
