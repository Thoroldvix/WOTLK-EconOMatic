package com.thoroldvix.g2gcalculator.dto;

import com.thoroldvix.g2gcalculator.model.Faction;
import com.thoroldvix.g2gcalculator.model.GameVersion;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record RealmResponse(
        Integer id,
        String name,
        Faction faction,
        @JsonProperty("version")
        GameVersion gameVersion,
        String region,
        @JsonProperty("auction house")
        AuctionHouseResponse auctionHouse
) {
}