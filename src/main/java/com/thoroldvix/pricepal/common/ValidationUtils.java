package com.thoroldvix.pricepal.common;

import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@UtilityClass
public class ValidationUtils {


    public void validatePositiveInt(int value,  String errorMessage) {
        if (value <= 0) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public void validateNonEmptyString(String value, String errorMessage) {
        Objects.requireNonNull(errorMessage, "Error cannot be null");
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public <T, E extends RuntimeException> void validateListNotEmpty(List<T> list, Supplier<E> exceptionSupplier) {
        Objects.requireNonNull(list, "List cannot be null");
        Objects.requireNonNull(exceptionSupplier, "Exception supplier cannot be null");
        if (list.isEmpty()) {
            throw exceptionSupplier.get();
        }
    }

}


