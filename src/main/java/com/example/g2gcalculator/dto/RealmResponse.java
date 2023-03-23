package com.example.g2gcalculator.dto;

import com.example.g2gcalculator.model.Faction;
import com.example.g2gcalculator.model.GameVersion;
import com.example.g2gcalculator.model.Region;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record RealmResponse(
        Integer id,
        String name,
        Faction faction,
        @JsonProperty("version")
        GameVersion gameVersion,
        String region,
        @JsonProperty("auction houses")
        List<AuctionHouseResponse> auctionHouses
) {
}