package com.thoroldvix.economatic.population;

import com.thoroldvix.economatic.server.Server;
import com.thoroldvix.economatic.shared.PaginationInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PopulationMapper {

    @Mapping(target = "server", source = "server", qualifiedByName = "serverName")
    PopulationResponse toResponse(Population population);


    List<PopulationResponse> toList(List<Population> populations);

     default PopulationListResponse toPopulationList(List<Population> populations) {
        return new PopulationListResponse(toList(populations));
    }

    default PopulationPageResponse toPageResponse(Page<Population> page) {
        List<PopulationResponse> populations = toList(page.getContent());
        return new PopulationPageResponse(new PaginationInfo(page), populations);
    }

    default TotalPopResponse toTotalPopResponse(TotalPopProjection totalPopProjection) {
        return TotalPopResponse.builder()
                .popAlliance(totalPopProjection.getPopAlliance())
                .popHorde(totalPopProjection.getPopHorde())
                .popTotal(totalPopProjection.getPopTotal())
                .serverName(totalPopProjection.getServerName())
                .build();
    }

    @Named("serverName")
    default String serverName(Server server) {
        try {
            return server.getUniqueName();
        } catch (NullPointerException e) {
            return null;
        }
    }
}
