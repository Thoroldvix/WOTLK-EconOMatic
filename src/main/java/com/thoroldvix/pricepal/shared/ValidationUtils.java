package com.thoroldvix.pricepal.shared;

import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;


public final class ValidationUtils {

    private ValidationUtils() {

    }

    public static void validatePositiveInt(int value, String errorMessage) {
        if (value <= 0) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static void validateStringNonNullOrEmpty(String value, String errorMessage) {
        Objects.requireNonNull(errorMessage, "Error message cannot be null");
        if (!hasText(value)) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static boolean isNumber(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static <T, E extends RuntimeException> void validateCollectionNotNullOrEmpty(Collection<T> list, Supplier<E> exceptionSupplier) {
        Objects.requireNonNull(list, "List cannot be null");
        Objects.requireNonNull(exceptionSupplier, "Exception supplier cannot be null");
        if (list.isEmpty()) {
            throw exceptionSupplier.get();
        }
    }

    public static boolean hasText(@Nullable String str) {
        return (str != null && !str.isEmpty() && containsText(str));
    }

    private static boolean containsText(CharSequence str) {
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

}


