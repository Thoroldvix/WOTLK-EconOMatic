package com.thoroldvix.g2gcalculator.item;

public enum ItemType {
    ARMOR,
    REAGENT,
    MONEY,
    QUIVER,
    MISCELLANEOUS,
    GLYPH,
    CONTAINER,
    KEY,
    PROJECTILE,
    WEAPON,
    CONSUMABLE,
    TRADE_GOODS,
    RECIPE,
    QUEST,
    GEM;

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase().replaceAll("_", " ");
    }
}
