package com.thoroldvix.pricepal.shared;


import com.thoroldvix.pricepal.error.NotFoundException;

import static com.thoroldvix.pricepal.shared.ValidationUtils.hasText;

public final class StringEnumConverter {

    private StringEnumConverter() {}

    public static <T extends Enum<T>> T fromString(String str, Class<T> enumClass) {
        if (!hasText(str)) {
            throw new IllegalArgumentException("Cannot convert blank or null string to enum");
        }
        for (T value : enumClass.getEnumConstants()) {
            String name = value.name().replace("_", " ");
            if (name.equalsIgnoreCase(str)) {
                return value;
            }
        }
        throw new NotFoundException(String.format("Could not find %s with itemName: %s", enumClass.getSimpleName(),
                str));
    }
}
