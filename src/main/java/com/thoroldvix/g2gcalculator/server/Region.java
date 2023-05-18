package com.thoroldvix.g2gcalculator.server;

import lombok.Getter;

import java.util.List;

@Getter
public enum Region {
    EU("ac3f85c1-7562-437e-b125-e89576b9a38e"),
    US(("dfced32f-2f0a-4df5-a218-1e068cfadffa")),
    FR("ac3f85c1-7562-437e-b125-e89576b9a38e"),
    DE("ac3f85c1-7562-437e-b125-e89576b9a38e"),
    ES("ac3f85c1-7562-437e-b125-e89576b9a38e"),
    OCE("dfced32f-2f0a-4df5-a218-1e068cfadffa"),
    RU("166fbf02-6d9a-45a0-9f74-ac3ba5a002b4");

    public static List<Region> getEURegions() {
        return List.of(EU, FR, DE, ES, RU);
    }

    public static List<Region> getUSRegions() {
        return List.of(US, OCE);
    }

    public Region getParentRegion() {
        return switch (this) {
            case EU, FR, ES, RU, DE -> EU;
            case US, OCE -> US;
        };
    }

    public static boolean contains(String region) {
        for (Region r : Region.values()) {
            if (r.name().equalsIgnoreCase(region)) {
                return true;
            }
        }
        return false;
    }

    public final String g2gId;

    Region(String g2gId) {
        this.g2gId = g2gId;
    }

}