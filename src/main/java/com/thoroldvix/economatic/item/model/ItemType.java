package com.thoroldvix.economatic.item.model;


import com.fasterxml.jackson.annotation.JsonValue;

public enum ItemType {
    CONSUMABLE,
    CONTAINER,
    WEAPON,
    GEM,
    ARMOR,
    REAGENT,
    PROJECTILE,
    TRADE_GOODS,
    RECIPE,
    QUIVER,
    QUEST,
    KEY,
    MISCELLANEOUS,
    GLYPH;

    @Override
    @JsonValue
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase().replace("_", " ");
    }
}
