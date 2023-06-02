package com.thoroldvix.pricepal.common;

import com.vaadin.flow.router.NotFoundException;
import org.springframework.util.StringUtils;

public class StringEnumConverter {

    public static <T extends Enum<T>> T fromString(String str, Class<T> enumClass) {
        if (!StringUtils.hasText(str)) {
            throw new IllegalArgumentException("Cannot convert blank or null string to enum");
        }
        for (T value : enumClass.getEnumConstants()) {
            String name = value.name().replaceAll("_", " ");
            if (name.equalsIgnoreCase(str)) {
                return value;
            }
        }
        throw new NotFoundException(String.format("Could not find %s with name: %s", enumClass.getSimpleName(),
                str));
    }
}
