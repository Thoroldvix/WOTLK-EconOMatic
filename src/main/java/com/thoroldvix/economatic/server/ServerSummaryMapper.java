package com.thoroldvix.economatic.server;

import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class ServerSummaryMapper {

    public ServerSummaryResponse toResponse(
            @NotNull(message = "Server summary projection cannot be null")
            ServerSummaryProjection summaryProjection) {
        ServerSummaryResponse.Summary summary = getSummary(summaryProjection);
        return ServerSummaryResponse.builder()
                .summary(summary)
                .build();
    }

    private ServerSummaryResponse.Summary getSummary(ServerSummaryProjection summaryProjection) {
        return ServerSummaryResponse.Summary.builder()
                .faction(getFaction(summaryProjection))
                .region(getRegion(summaryProjection))
                .locale(getLocale(summaryProjection))
                .type(getType(summaryProjection))
                .total(summaryProjection.getTotal())
                .build();
    }

    private ServerSummaryResponse.Type getType(ServerSummaryProjection summaryProjection) {
        return ServerSummaryResponse.Type.builder()
                .pve(summaryProjection.getPve())
                .pvp(summaryProjection.getPvp())
                .pvpRp(summaryProjection.getPvpRp())
                .rp(summaryProjection.getRp())
                .build();
    }

    private ServerSummaryResponse.Locale getLocale(ServerSummaryProjection summaryProjection) {
        return ServerSummaryResponse.Locale.builder()
                .enGB(summaryProjection.getEnGB())
                .ruRU(summaryProjection.getRuRU())
                .deDE(summaryProjection.getDeDE())
                .enUS(summaryProjection.getEnUS())
                .esES(summaryProjection.getEsES())
                .frFR(summaryProjection.getFrFR())
                .build();
    }

    private ServerSummaryResponse.Region getRegion(ServerSummaryProjection summaryProjection) {
        return ServerSummaryResponse.Region.builder()
                .eu(summaryProjection.getEu())
                .us(summaryProjection.getUs())
                .build();
    }

    private ServerSummaryResponse.Faction getFaction(ServerSummaryProjection summaryProjection) {
        return ServerSummaryResponse.Faction.builder()
                .alliance(summaryProjection.getAlliance())
                .horde(summaryProjection.getHorde())
                .build();
    }
}
