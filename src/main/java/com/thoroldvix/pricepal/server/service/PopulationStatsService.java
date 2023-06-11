package com.thoroldvix.pricepal.server.service;

import com.thoroldvix.pricepal.common.dto.RequestDto;
import com.thoroldvix.pricepal.common.service.SearchSpecification;
import com.thoroldvix.pricepal.server.dto.PopulationResponse;
import com.thoroldvix.pricepal.server.dto.StatsResponse;
import com.thoroldvix.pricepal.server.entity.Population;
import com.thoroldvix.pricepal.server.repository.StatisticsDao;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;

import static com.thoroldvix.pricepal.common.util.ValidationUtils.validateNonNullOrEmptyString;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PopulationStatsService {
    private final PopulationMapper populationMapper;
    private final SearchSpecification<Population> searchSpecification;
    private final StatisticsDao<Population> statisticsDao;

    public StatsResponse<PopulationResponse> getStatsForServer(String serverIdentifier) {
        validateNonNullOrEmptyString(serverIdentifier, "Server identifier cannot be null or empty");
        Specification<Population> spec = searchSpecification.getJoinSpecForServerIdentifier(serverIdentifier);
        Map<String, Object> statisticsResponse = statisticsDao.getStatsForSpec(spec, Population.class);
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

    public StatsResponse<PopulationResponse> getStatsForAll(int timeRangeInDays) {
        Specification<Population> spec = searchSpecification.getSpecForTimeRange(timeRangeInDays);
        Map<String, Object> statisticsResponse = statisticsDao.getStatsForSpec(spec, Population.class);
        return createStatsResponse(statisticsResponse);
    }

    public StatsResponse<PopulationResponse> getStatsForSearch(RequestDto requestDto) {
        Objects.requireNonNull(requestDto, "RequestDto cannot be null");
        Specification<Population> spec = searchSpecification.createSearchSpecification(requestDto.searchCriteria(),
                requestDto.globalOperator());

        Map<String, Object> statisticsResponse = statisticsDao.getStatsForSpec(spec, Population.class);
        return createStatsResponse(statisticsResponse);
    }
}
