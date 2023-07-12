package com.thoroldvix.economatic.summary.item;

import lombok.Builder;

@Builder
public record ItemSummaryResponse(
        Summary summary
) {
    @Builder
    public record Summary(
            Quality quality,

            Slot slot,

            Type type,

            int total
    ) {
    }

    @Builder
    public record Quality(
            int common,
            int uncommon,
            int rare,
            int epic,
            int legendary
    ) {
    }

    @Builder
    public record Slot(
            int nonEquipable,
            int head,
            int neck,
            int shoulder,
            int shirt,
            int chest,
            int waist,
            int legs,
            int feet,
            int wrists,
            int hands,
            int finger,
            int trinket,
            int weapon,
            int shield,
            int ranged,
            int back,
            int twoHand,
            int bag,
            int tabard,
            int robe,
            int mainHand,
            int offHand,
            int holdable,
            int ammo,
            int thrown,
            int rangedRight,
            int quiver,
            int relic
    ) {
    }

    @Builder
    public record Type(int consumable,
                          int container,
                          int weapon,
                          int gem,
                          int armor,
                          int reagent,
                          int projectile,
                          int tradeGoods,
                          int recipe,
                          int quiver,
                          int quest,
                          int key,
                          int miscellaneous,
                          int glyph
    ) {

    }
}

