package com.thoroldvix.economatic.shared;

import java.time.LocalDateTime;


public record TimeRange(
        LocalDateTime start,
        LocalDateTime end
) {
    public TimeRange(int days) {
        this(LocalDateTime.now().minusDays(days), LocalDateTime.now());
        if (days < 1) {
            throw new NumberNotPositiveException("Time range should be at least 1 day");
        }
    }
}
