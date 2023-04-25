package com.thoroldvix.g2gcalculator.common;

public class StringEnumConverter<T extends Enum<T>> {
    private final Class<T> enumClass;

    public StringEnumConverter(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    public T fromString(String str) {
        for (T value : enumClass.getEnumConstants()) {
            String name = value.name().replaceAll("_", " ");
            if (name.equalsIgnoreCase(str)) {
                return value;
            }
        }
        throw new NotFoundException("Could not find enum for " + str);
    }
}
