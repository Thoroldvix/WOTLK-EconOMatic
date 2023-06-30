package com.thoroldvix.economatic.shared;

import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Validated
public record TimeRange(
        LocalDateTime start,
        LocalDateTime end
) {
    public TimeRange(@Min(value = 1, message = "Time range should be at least 1 day") int days) {
        this(LocalDateTime.now().minusDays(days), LocalDateTime.now());
    }
}
