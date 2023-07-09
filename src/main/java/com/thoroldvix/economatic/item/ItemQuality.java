package com.thoroldvix.economatic.item;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ItemQuality {
    COMMON,
    UNCOMMON,
    RARE,
    EPIC,
    LEGENDARY;

    @Override
    @JsonValue
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
