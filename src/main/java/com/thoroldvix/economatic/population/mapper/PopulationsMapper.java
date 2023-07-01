package com.thoroldvix.economatic.population.mapper;

import com.thoroldvix.economatic.population.dto.PopulationResponse;
import com.thoroldvix.economatic.population.dto.PopulationsPagedResponse;
import com.thoroldvix.economatic.population.dto.PopulationsResponse;
import com.thoroldvix.economatic.population.dto.TotalPopResponse;
import com.thoroldvix.economatic.population.model.Population;
import com.thoroldvix.economatic.population.repository.TotalPopProjection;
import com.thoroldvix.economatic.shared.dto.Filters;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.util.List;

import static com.thoroldvix.economatic.shared.error.ErrorMessages.PAGE_CANNOT_BE_NULL;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class PopulationsMapper {
    private static final String POPULATIONS_CANNOT_BE_NULL_OR_EMPTY = "Populations cannot be null or empty";
    private static final PopulationMapper POPULATION_MAPPER = Mappers.getMapper(PopulationMapper.class);

    public PopulationsPagedResponse toPagedWithServer(@NotNull(message = PAGE_CANNOT_BE_NULL) Page<Population> page) {
        return createPagedPopulationsResponse(page, true);
    }

    public PopulationsPagedResponse toPaged(@NotNull(message = PAGE_CANNOT_BE_NULL) Page<Population> page) {
        return createPagedPopulationsResponse(page, false);
    }

    public PopulationsResponse toResponse(@NotEmpty(message = POPULATIONS_CANNOT_BE_NULL_OR_EMPTY) List<Population> prices) {
        return makePopulationsResponse(null, POPULATION_MAPPER.toResponseListWithServer(prices));
    }

    public PopulationsResponse toRegionResponse(@NotEmpty(message = POPULATIONS_CANNOT_BE_NULL_OR_EMPTY) List<Population> prices) {
        String region = prices.get(0).getServer().getRegion().toString();
        return makePopulationsResponse(Filters.builder().region(region).build(), POPULATION_MAPPER.toResponseListWithServer(prices));
    }

    public PopulationsResponse toFactionResponse(@NotEmpty(message = POPULATIONS_CANNOT_BE_NULL_OR_EMPTY) List<Population> prices) {
        String faction = prices.get(0).getServer().getFaction().toString();
        return makePopulationsResponse(Filters.builder().faction(faction).build(), POPULATION_MAPPER.toResponseListWithServer(prices));
    }

    public PopulationsResponse toServerResponse(@NotEmpty(message = POPULATIONS_CANNOT_BE_NULL_OR_EMPTY) List<Population> prices) {
        String serverUniqueName = prices.get(0).getServer().getUniqueName();
        return makePopulationsResponse(Filters.builder().server(serverUniqueName).build(), POPULATION_MAPPER.toResponseList(prices));
    }

    public TotalPopResponse toTotalPopResponse(TotalPopProjection totalPopProjection) {
        return TotalPopResponse.builder()
                .popAlliance(totalPopProjection.getPopAlliance())
                .popHorde(totalPopProjection.getPopHorde())
                .popTotal(totalPopProjection.getPopTotal())
                .serverName(totalPopProjection.getServerName())
                .build();
    }

    private PopulationsResponse makePopulationsResponse(Filters filters, List<PopulationResponse> populations) {
        return PopulationsResponse.builder()
                .filters(filters)
                .populations(populations)
                .build();
    }

    private PopulationsPagedResponse createPagedPopulationsResponse(Page<Population> page, boolean withServer) {
        PopulationsResponse populationsResponse = withServer ? toServerResponse(page.getContent()) : toResponse(page.getContent());

        return PopulationsPagedResponse.builder()
                .populationsResponse(populationsResponse)
                .page(page.getNumber())
                .pageSize(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .build();
    }


}
