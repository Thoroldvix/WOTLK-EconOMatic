package com.thoroldvix.economatic.util;

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


    public  static  <T> T checkNullAndGet(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (NullPointerException e) {
            return null;
        }
    }


}
