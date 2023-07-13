package com.thoroldvix.economatic.shared;

import com.thoroldvix.economatic.error.InvalidTimeRangeException;

import java.time.LocalDateTime;


public record TimeRange(
        LocalDateTime start,
        LocalDateTime end
) {
    public TimeRange(int days) {
        this(LocalDateTime.now().minusDays(days), LocalDateTime.now());
        if (days < 1) {
            throw new InvalidTimeRangeException("Time range cannot be less than 1 day");
        }
    }
}
