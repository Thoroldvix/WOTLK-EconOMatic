package com.thoroldvix.economatic.summary.server;

import lombok.Builder;

@Builder
public record ServerSummaryResponse(
        Summary summary
) {

    @Builder
    public record Summary(
            Faction faction,
            Region region,
            Type type,
            Locale locale,
            int total
    ) {

    }

    @Builder
    public record Faction(
            int alliance,
            int horde
    ) {

    }

    @Builder
    public record Region(
            int eu,
            int us
    ) {

    }

    @Builder
    public record Type(
            int pve,
            int pvp,
            int pvpRp,
            int rp
    ) {

    }

    @Builder
    public record Locale(
            int enGB,
            int enUS,
            int deDE,
            int esES,
            int frFR,
            int ruRU
    ) {

    }
}
