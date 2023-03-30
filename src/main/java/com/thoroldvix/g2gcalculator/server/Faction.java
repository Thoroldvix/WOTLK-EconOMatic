package com.thoroldvix.g2gcalculator.server;

public enum Faction {
    ALLIANCE,
    HORDE;


    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }

    public static boolean contains(String faction) {
        for (Faction f : Faction.values()) {
            if (f.name().equalsIgnoreCase(faction)) {
                return true;
            }
        }
        return false;
    }
}