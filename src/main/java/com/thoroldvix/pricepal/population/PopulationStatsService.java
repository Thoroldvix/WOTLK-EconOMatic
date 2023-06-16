package com.thoroldvix.pricepal.population;

import com.thoroldvix.pricepal.shared.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;

import static com.thoroldvix.pricepal.shared.ServerSearchCriteriaBuilder.getJoinCriteria;
import static com.thoroldvix.pricepal.shared.ValidationUtils.validateNonNullOrEmptyString;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PopulationStatsService {
    private final PopulationMapper populationMapper;
    private final SearchSpecification<Population> searchSpecification;
    private final StatisticsRepository<Population> statisticsRepositoryImpl;

    public StatsResponse<PopulationResponse> getForServer(String serverIdentifier) {
        validateNonNullOrEmptyString(serverIdentifier, "Server identifier cannot be null or empty");
        SearchCriteria joinCriteria = getJoinCriteria(serverIdentifier);
        Specification<Population> spec = searchSpecification.createSearchSpecification(RequestDto.GlobalOperator.AND, joinCriteria);
        Map<String, Object> statisticsResponse = statisticsRepositoryImpl.getStats(spec, Population.class);
        return createStatsResponse(statisticsResponse);
    }

    private StatsResponse<PopulationResponse> createStatsResponse(Map<String, Object> statisticsResponse) {
        int average = (int) Math.floor((double) statisticsResponse.get("avg"));

        Population maxPopulationSize = (Population) statisticsResponse.get("max");
        Population minPopulationSize = (Population) statisticsResponse.get("min");

        PopulationResponse min = populationMapper.toPopulationResponse(minPopulationSize);
        PopulationResponse max = populationMapper.toPopulationResponse(maxPopulationSize);


        long count = (long) statisticsResponse.get("count");

        return StatsResponse.<PopulationResponse>builder()
                .average(average)
                .maximum(max)
                .minimum(min)
                .count(count)
                .build();
    }

    public StatsResponse<PopulationResponse> getForAll(int timeRangeInDays) {
        Specification<Population> spec = searchSpecification.getSpecForTimeRange(timeRangeInDays);
        Map<String, Object> statisticsResponse = statisticsRepositoryImpl.getStats(spec, Population.class);
        return createStatsResponse(statisticsResponse);
    }

    public StatsResponse<PopulationResponse> getForSearch(RequestDto requestDto) {
        Objects.requireNonNull(requestDto, "RequestDto cannot be null");
        Specification<Population> spec = searchSpecification.createSearchSpecification(requestDto.globalOperator(),
                requestDto.searchCriteria());

        Map<String, Object> statisticsResponse = statisticsRepositoryImpl.getStats(spec, Population.class);
        return createStatsResponse(statisticsResponse);
    }
}
