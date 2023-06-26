package com.thoroldvix.economatic.server;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ServerSummaryMapper {

    default ServerSummaryResponse toResponse(ServerSummaryProjection summaryProjection) {
        ServerSummaryResponse.Faction faction = getFaction(summaryProjection);
        ServerSummaryResponse.Region region = getRegion(summaryProjection);
        ServerSummaryResponse.Type type = getType(summaryProjection);
        ServerSummaryResponse.Locale locale = getLocale(summaryProjection);

        return ServerSummaryResponse.builder()
                .faction(faction)
                .region(region)
                .locale(locale)
                .type(type)
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

    private  ServerSummaryResponse.Locale getLocale(ServerSummaryProjection summaryProjection) {
        return ServerSummaryResponse.Locale.builder()
                .enGB(summaryProjection.getEnGB())
                .ruRU(summaryProjection.getRuRU())
                .deDE(summaryProjection.getDeDE())
                .enUS(summaryProjection.getEnUS())
                .esES(summaryProjection.getEsES())
                .frFR(summaryProjection.getFrFR())
                .build();
    }

    private  ServerSummaryResponse.Region getRegion(ServerSummaryProjection summaryProjection) {
        return ServerSummaryResponse.Region.builder()
                .eu(summaryProjection.getEu())
                .us(summaryProjection.getUs())
                .build();
    }

    private  ServerSummaryResponse.Faction getFaction(ServerSummaryProjection summaryProjection) {
        return ServerSummaryResponse.Faction.builder()
                .alliance(summaryProjection.getAlliance())
                .horde(summaryProjection.getHorde())
                .build();
    }
}
