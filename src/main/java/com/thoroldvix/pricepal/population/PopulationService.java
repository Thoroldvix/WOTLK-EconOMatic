package com.thoroldvix.pricepal.population;

import com.thoroldvix.pricepal.server.Faction;
import com.thoroldvix.pricepal.server.Region;
import com.thoroldvix.pricepal.shared.RequestDto;
import com.thoroldvix.pricepal.shared.SearchCriteria;
import com.thoroldvix.pricepal.shared.SearchSpecification;
import com.thoroldvix.pricepal.shared.StringEnumConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.thoroldvix.pricepal.shared.ServerSearchCriteriaBuilder.getJoinCriteria;
import static com.thoroldvix.pricepal.shared.ValidationUtils.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PopulationService {

    private final PopulationRepository populationRepository;
    private final PopulationMapper populationMapper;
    private final SearchSpecification<Population> searchSpecification;


    public List<PopulationResponse> getAll(int timeRangeInDays, Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable cannot be null");
        Specification<Population> spec = searchSpecification.getSpecForTimeRange(timeRangeInDays);
        List<Population> populations = populationRepository.findAll(spec, pageable).getContent();
        validateCollectionNotNullOrEmpty(populations,
                () -> new PopulationNotFoundException("No populations found"));
        return populationMapper.toResponseList(populations);
    }

    public List<PopulationResponse> getForServer(String serverIdentifier,
                                                 Pageable pageable) {
        validateStringNonNullOrEmpty(serverIdentifier, "Server identifier cannot be null or empty");
        SearchCriteria serverCriteria = getJoinCriteria(serverIdentifier);
        Specification<Population> spec = searchSpecification.createSearchSpecification(RequestDto.GlobalOperator.AND, serverCriteria);
        List<Population> populations = populationRepository.findAll(spec, pageable).getContent();
        validateCollectionNotNullOrEmpty(populations,
                () -> new PopulationNotFoundException("No populations found for server identifier: " + serverIdentifier));
        return populationMapper.toResponseList(populations);
    }

    @Transactional
    public void saveAll(List<Population> populations) {
        validateCollectionNotNullOrEmpty(populations,
                () -> new IllegalArgumentException("Population list cannot be empty"));
        populationRepository.saveAll(populations);
    }

    public TotalPopResponse getTotalPopulation(String serverName) {
        validateStringNonNullOrEmpty(serverName, "Server name cannot be null or empty");
        TotalPopProjection totalPopProjection = populationRepository.findTotalPopulationForServerName(serverName)
                .orElseThrow(() -> new PopulationNotFoundException("No population found for server name " + serverName));

        return TotalPopResponse.builder()
                .popAlliance(totalPopProjection.getPopAlliance())
                .popHorde(totalPopProjection.getPopHorde())
                .popTotal(totalPopProjection.getPopTotal())
                .name(totalPopProjection.getServerName())
                .build();
    }

    public List<PopulationResponse> search(RequestDto requestDto, Pageable pageable) {
        Objects.requireNonNull(requestDto, "RequestDto cannot be null");
        Objects.requireNonNull(pageable, "Pageable cannot be null");
        Specification<Population> spec = searchSpecification.createSearchSpecification(requestDto.globalOperator(),
                requestDto.searchCriteria());

        List<Population> populations = populationRepository.findAll(spec, pageable).getContent();
        validateCollectionNotNullOrEmpty(populations,
                () -> new PopulationNotFoundException("No populations found"));

        return populationMapper.toResponseList(populations);
    }

    public List<PopulationResponse> getAllRecent(Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable cannot be null");
        List<Population> populations = populationRepository.findAllRecent(pageable).getContent();
        validateCollectionNotNullOrEmpty(populations,
                () -> new PopulationNotFoundException("No populations found"));
        return populationMapper.toResponseList(populations);
    }

    public PopulationResponse getRecentForServer(String serverIdentifier) {
        validateStringNonNullOrEmpty(serverIdentifier, "Server identifier cannot be null or empty");
        Optional<Population> population = findRecentForServer(serverIdentifier);
        return population.map(populationMapper::toResponse)
                .orElseThrow(() -> new PopulationNotFoundException("No recent population found for server identifier: " + serverIdentifier));
    }

    public List<PopulationResponse> getRecentForRegion(String regionName) {
        Region region = StringEnumConverter.fromString(regionName, Region.class);
        List<Population> population = populationRepository.findRecentForRegion(region);
        List<PopulationResponse> populations = populationMapper.toResponseList(population);
        validateCollectionNotNullOrEmpty(populations, () -> new PopulationNotFoundException("No recent populations found for region: " + regionName));
        return populations;
    }

    public List<PopulationResponse> getRecentForFaction(String factionName) {
        Faction faction = StringEnumConverter.fromString(factionName, Faction.class);
        List<Population> population = populationRepository.findRecentForFaction(faction);
        List<PopulationResponse> populations = populationMapper.toResponseList(population);
        validateCollectionNotNullOrEmpty(populations, () -> new PopulationNotFoundException("No recent populations found for faction: " + factionName));
        return populations;
    }

    private Optional<Population> findRecentForServer(String serverIdentifier) {
        try {
            int serverId = Integer.parseInt(serverIdentifier);
            return populationRepository.findRecentByServerId(serverId);
        } catch (NumberFormatException e) {
            return populationRepository.findRecentByServerUniqueName(serverIdentifier);
        }
    }

}

