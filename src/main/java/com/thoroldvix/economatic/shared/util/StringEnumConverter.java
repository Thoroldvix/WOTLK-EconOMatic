package com.thoroldvix.economatic.shared.util;


import com.thoroldvix.economatic.error.NotFoundException;

import static com.thoroldvix.economatic.shared.util.ValidationUtils.isNonEmptyString;

public final class StringEnumConverter {

    private StringEnumConverter() {}

    public static <T extends Enum<T>> T fromString(String str, Class<T> enumClass) {
        if (!isNonEmptyString(str)) {
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
