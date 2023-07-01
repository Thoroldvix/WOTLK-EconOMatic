package com.thoroldvix.economatic.item.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ItemSlot {
    NON_EQUIPABLE,
    HEAD,
    NECK,
    SHOULDER,
    SHIRT,
    CHEST,
    WAIST,
    LEGS,
    FEET,
    WRISTS,
    HANDS,
    FINGER,
    TRINKET,
    WEAPON,
    SHIELD,
    RANGED,
    BACK,
    TWO_HAND,
    BAG,
    TABARD,
    ROBE,
    MAIN_HAND,
    OFF_HAND,
    HOLDABLE,
    AMMO,
    THROWN,
    RANGED_RIGHT,
    QUIVER,
    RELIC;

    @Override
    @JsonValue
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase().replace("_", " ");
    }
}
