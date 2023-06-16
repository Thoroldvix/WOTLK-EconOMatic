package com.thoroldvix.pricepal.server;

import lombok.Getter;

@Getter
public enum Region {
    EU,
    US;
    public static boolean contains(String region) {
        for (Region r : Region.values()) {
            if (r.name().equalsIgnoreCase(region)) {
                return true;
            }
        }
        return false;
    }
}