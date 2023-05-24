package com.thoroldvix.pricepal.item.entity;


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
