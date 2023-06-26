package com.thoroldvix.economatic.server;

import lombok.Builder;

@Builder
public record ServerSummaryResponse(
        Faction faction,
        Region region,
        Type type,
        Locale locale,
        int total

) {
    @Builder
    protected record Faction(
            int alliance,
            int horde
    ) {
    }
    @Builder
    protected record Region(
            int eu,
            int us
    ) {
    }
    @Builder
    protected record Type(
            int pve,
            int pvp,
            int pvpRp,
            int rp
    ) {
    }
    @Builder
    protected record Locale(
            int enGB,
            int enUS,
            int deDE,
            int esES,
            int frFR,
            int ruRU
    ) {
    }
}
