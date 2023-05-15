package com.thoroldvix.g2gcalculator.item;

public enum ItemQuality {
    POOR,
    COMMON,
    UNCOMMON,
    RARE,
    EPIC,
    LEGENDARY;

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
