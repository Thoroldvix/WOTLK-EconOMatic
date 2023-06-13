package com.thoroldvix.pricepal.item;

public enum ItemQuality {
    POOR,
    COMMON,
    UNCOMMON,
    RARE,
    EPIC,
    LEGENDARY,
    HEIRLOOM;

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
