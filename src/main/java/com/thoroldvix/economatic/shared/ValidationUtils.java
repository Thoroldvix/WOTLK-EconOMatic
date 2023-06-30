package com.thoroldvix.economatic.shared;

import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.IntStream;

@Validated
public final class ValidationUtils {

    private ValidationUtils() {}

    public static <T, E extends RuntimeException> void validateCollectionNotEmpty(@Nullable Collection<T> list, Supplier<E> exceptionSupplier) {
        Objects.requireNonNull(exceptionSupplier, "Exception supplier cannot be null");
        if (list == null || list.isEmpty()) {
            throw exceptionSupplier.get();
        }
    }

    public static boolean hasText(@Nullable String str) {
        return (str != null && !str.isEmpty() && containsText(str));
    }

    private static boolean containsText(CharSequence str) {
        return IntStream.range(0, str.length())
                .anyMatch(i -> !Character.isWhitespace(str.charAt(i)));
    }
}


