package com.example.g2gcalculator.model;

public enum Faction {
    ALLIANCE,
    HORDE;

    public static boolean contains(String faction) {
        for (Faction f : Faction.values()) {
            if (f.name().equalsIgnoreCase(faction)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}