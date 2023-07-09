package com.thoroldvix.economatic.shared;

import com.thoroldvix.economatic.error.StatisticsNotFoundException;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;

public final class ValidationUtils {

    private ValidationUtils() {
    }

    public static <T, E extends RuntimeException> void notEmpty(Collection<T> collection, Supplier<E> exceptionSupplier) {
        Objects.requireNonNull(exceptionSupplier, "Exception supplier cannot be null");
        if (isCollectionEmpty(collection)) {
            throw exceptionSupplier.get();
        }
    }

    public static void validateStatsProjection(StatsProjection statsProjection) {
        boolean isInvalid = statsProjection.getMean() == null
                            || statsProjection.getMaxId() == null
                            || statsProjection.getMinId() == null
                            || statsProjection.getMedian() == null;
        if (isInvalid) {
            throw new StatisticsNotFoundException("No statistics found");
        }
    }

    public static <T, E extends RuntimeException> void notEmpty(T[] array, Supplier<E> exceptionSupplier) {
        Objects.requireNonNull(exceptionSupplier, "Exception supplier cannot be null");
        if (array == null || array.length == 0) {
            throw exceptionSupplier.get();
        }
    }

    public static boolean isNonEmptyString(String str) {
        return str != null && !str.isBlank();
    }

    public static void notEmpty(String value, String errorMessage) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static void notLessThan(int value, int minThreshold, String errorMessage) {
        if (value < minThreshold) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static <T> boolean isCollectionEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    public static void inRange(int value, int minThreshold, int maxThreshold, String errorMessage) {
        if (value < minThreshold || value > maxThreshold) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public  static  <T> T checkNullAndGet(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (NullPointerException e) {
            return null;
        }
    }


}
