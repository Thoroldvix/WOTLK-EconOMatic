package com.thoroldvix.economatic.util;

import java.time.Duration;
import java.time.Instant;

public final class Utils {

    private Utils() {}

     public static long elapsedTimeInMillis(Instant start) {
        return Duration.between(start, Instant.now()).toMillis();
    }
}
