package com.thoroldvix.pricepal.population;

import com.thoroldvix.pricepal.server.Server;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Objects;

import static com.thoroldvix.pricepal.shared.ErrorMessages.PAGE_CANNOT_BE_NULL;
import static com.thoroldvix.pricepal.shared.ValidationUtils.validateCollectionNotNullOrEmpty;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PopulationMapper {

    String POPULATIONS_CANNOT_BE_NULL_OR_EMPTY = "Populations cannot be null or empty";

    @Mapping(target = "server", source = "server", qualifiedByName = "serverName")
    PopulationResponse toResponseWithServer(Population population);

    @Mapping(target = "server", ignore = true)
    PopulationResponse toResponse(Population population);

    default List<PopulationResponse> toResponseList(List<Population> populations) {
        validateCollectionNotNullOrEmpty(populations,
                () -> new IllegalArgumentException(POPULATIONS_CANNOT_BE_NULL_OR_EMPTY));
        return populations.stream().map(this::toResponse).toList();
    }

    default List<PopulationResponse> toResponseListWithServer(List<Population>
                                                                      populations) {
        validateCollectionNotNullOrEmpty(populations,
                () -> new IllegalArgumentException(POPULATIONS_CANNOT_BE_NULL_OR_EMPTY));
        return populations.stream().map(this::toResponseWithServer).toList();
    }

    default PopulationsPagedResponse toPagedResponse(Page<Population> page) {
        Objects.requireNonNull(page, PAGE_CANNOT_BE_NULL);
        PopulationsResponse populationsResponse = getPopulationsResponse(page.getContent());
        return PopulationsPagedResponse.builder()
                .page(page.getNumber())
                .pageSize(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .populationsResponse(populationsResponse)
                .build();
    }

    default PopulationsPagedResponse toPagedWithServer(Page<Population> page) {
        Objects.requireNonNull(page, PAGE_CANNOT_BE_NULL);
        PopulationsResponse populationsResponse = getPopulationsResponse(page.getContent());
        return PopulationsPagedResponse.builder()
                .page(page.getNumber())
                .pageSize(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .populationsResponse(populationsResponse)
                .build();
    }

    default PopulationsResponse toResponseWithRegion(List<Population> populations) {
        validateCollectionNotNullOrEmpty(populations,
                () -> new IllegalArgumentException(POPULATIONS_CANNOT_BE_NULL_OR_EMPTY));
        List<PopulationResponse> populationResponses = toResponseListWithServer(populations);
        String regionName = populations.get(0).getServer().getRegion().toString();
        return PopulationsResponse.builder()
                .region(regionName)
                .populations(populationResponses)
                .build();
    }

        default TotalPopResponse toTotalPopResponse(TotalPopProjection totalPopProjection) {
        return TotalPopResponse.builder()
                .popAlliance(totalPopProjection.getPopAlliance())
                .popHorde(totalPopProjection.getPopHorde())
                .popTotal(totalPopProjection.getPopTotal())
                .serverName(totalPopProjection.getServerName())
                .build();
    }

      default PopulationsResponse toResponseWithFaction(List<Population> populations) {
         validateCollectionNotNullOrEmpty(populations,
                () -> new IllegalArgumentException(POPULATIONS_CANNOT_BE_NULL_OR_EMPTY));
        List<PopulationResponse> populationResponses = toResponseListWithServer(populations);
        String factionName = populations.get(0).getServer().getFaction().toString();
        return PopulationsResponse.builder()
                .faction(factionName)
                .populations(populationResponses)
                .build();
    }

    private PopulationsResponse getPopulationsResponse(List<Population> populations) {
        validateCollectionNotNullOrEmpty(populations,
                () -> new IllegalArgumentException(POPULATIONS_CANNOT_BE_NULL_OR_EMPTY));
        List<PopulationResponse> populationResponses = toResponseListWithServer(populations);
        return PopulationsResponse.builder()
                .populations(populationResponses)
                .build();
    }


    @Named("serverName")
    default String serverName(Server server) {
        Objects.requireNonNull(server, "Server cannot be null");
        return server.getUniqueName();
    }




}
