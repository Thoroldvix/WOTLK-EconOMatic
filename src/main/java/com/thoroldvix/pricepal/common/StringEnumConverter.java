package com.thoroldvix.pricepal.common;

import com.vaadin.flow.router.NotFoundException;

public class StringEnumConverter {

    public static <T extends Enum<T>> T fromString(String str, Class<T> enumClass) {

        for (T value : enumClass.getEnumConstants()) {
            String name = value.name().replaceAll("_", " ");
            if (name.equalsIgnoreCase(str)) {
                return value;
            }
        }
        throw new NotFoundException(String.format("Could not find %s with name: %s", enumClass.getSimpleName().toLowerCase(),
                str));
    }
}
