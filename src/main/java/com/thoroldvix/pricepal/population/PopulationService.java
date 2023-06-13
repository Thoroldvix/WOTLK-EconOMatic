package com.thoroldvix.pricepal.population;

import com.thoroldvix.pricepal.shared.RequestDto;
import com.thoroldvix.pricepal.shared.SearchSpecification;
import com.thoroldvix.pricepal.goldprice.GoldPriceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.thoroldvix.pricepal.shared.ValidationUtils.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PopulationService {
    private final PopulationRepository populationRepository;
    private final PopulationMapper populationMapper;
    private final SearchSpecification<Population> searchSpecification;


    public List<PopulationResponse> getAllPopulations(int timeRangeInDays, Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable cannot be null");
        Specification<Population> spec = searchSpecification.getSpecForTimeRange(timeRangeInDays);
        List<Population> populations = populationRepository.findAll(spec, pageable).getContent();
        validateListNotNullOrEmpty(populations,
                () -> new PopulationNotFoundException("No populations found"));
        return populationMapper.toPopulationResponseList(populations);
    }

    public List<PopulationResponse> getPopulationForServer(String serverIdentifier,
                                                           Pageable pageable) {
        validateNonNullOrEmptyString(serverIdentifier, "Server identifier cannot be null or empty");
        Specification<Population> spec = searchSpecification.getJoinSpecForServerIdentifier(serverIdentifier);
        List<Population> populations = populationRepository.findAll(spec, pageable).getContent();
        validateListNotNullOrEmpty(populations,
                () -> new PopulationNotFoundException("No populations found for server identifier: " + serverIdentifier));

        return populationMapper.toPopulationResponseList(populations);
    }
    @Transactional
    public void saveAllPopulations(List<Population> populations) {
        validateListNotNullOrEmpty(populations,
                () -> new IllegalArgumentException("Population list cannot be empty"));
        populationRepository.saveAll(populations);
    }

    public TotalPopResponse getTotalPopulationForServerName(String serverName) {
        validateNonNullOrEmptyString(serverName, "Server name cannot be null or empty");
        TotalPopProjection totalPopProjection = populationRepository.findTotalPopulationForServerName(serverName)
                .orElseThrow(() -> new PopulationNotFoundException("No population found for server name " + serverName));

        return TotalPopResponse.builder()
                .alliancePop(totalPopProjection.getAlliancePop())
                .hordePop(totalPopProjection.getHordePop())
                .totalPop(totalPopProjection.getTotalPop())
                .serverName(totalPopProjection.getServerName())
                .build();
    }

    public List<PopulationResponse> searchForPopulation(RequestDto requestDto, Pageable pageable) {
        Objects.requireNonNull(requestDto, "RequestDto cannot be null");
        Objects.requireNonNull(pageable, "Pageable cannot be null");
        Specification<Population> spec = searchSpecification.createSearchSpecification(requestDto.searchCriteria(),
                requestDto.globalOperator());

        List<Population> populations = populationRepository.findAll(spec, pageable).getContent();
        validateListNotNullOrEmpty(populations,
                () -> new PopulationNotFoundException("No populations found"));

        return populationMapper.toPopulationResponseList(populations);
    }

    public List<PopulationResponse> getAllRecentPopulations(Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable cannot be null");
        List<Population> populations = populationRepository.findAllRecent(pageable).getContent();
        validateListNotNullOrEmpty(populations,
                () -> new PopulationNotFoundException("No populations found"));
        return populationMapper.toPopulationResponseList(populations);
    }

    public PopulationResponse getRecentForServer(String serverIdentifier) {
        validateNonNullOrEmptyString(serverIdentifier, "Server identifier cannot be null or empty");
        Population population;
        if (isNumber(serverIdentifier)) {
            int serverId = Integer.parseInt(serverIdentifier);
            population = populationRepository.findRecentByServerId(serverId)
                    .orElseThrow(() -> new PopulationNotFoundException("No recent population found for server identifier: " + serverIdentifier));
        } else {
            population = populationRepository.findRecentByServerUniqueName(serverIdentifier)
                    .orElseThrow(() -> new GoldPriceNotFoundException("No recent population found for server identifier: " + serverIdentifier));
        }
        return populationMapper.toPopulationResponse(population);
    }
}

