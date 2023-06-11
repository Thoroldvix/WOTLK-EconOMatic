package com.thoroldvix.pricepal.server.service;

import com.thoroldvix.pricepal.common.dto.RequestDto;
import com.thoroldvix.pricepal.common.service.SearchSpecification;
import com.thoroldvix.pricepal.server.dto.ServerPriceResponse;
import com.thoroldvix.pricepal.server.dto.StatsResponse;
import com.thoroldvix.pricepal.server.entity.ServerPrice;
import com.thoroldvix.pricepal.server.error.StatisticsNotFoundException;
import com.thoroldvix.pricepal.server.repository.StatisticsDao;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Objects;

import static com.thoroldvix.pricepal.common.util.ValidationUtils.validateNonNullOrEmptyString;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ServerPriceStatsService {
    private final StatisticsDao<ServerPrice> statisticsDao;
    private final SearchSpecification<ServerPrice> searchSpecification;
    private final ServerPriceMapper serverPriceMapper;

    public StatsResponse<ServerPriceResponse> getStatsForSearch(RequestDto requestDto) {
        Objects.requireNonNull(requestDto, "RequestDto cannot be null");
        Specification<ServerPrice> spec;
        spec = searchSpecification.createSearchSpecification(requestDto.searchCriteria(), requestDto.globalOperator());

        Map<String, Object> statisticsResponse = statisticsDao.getStatsForSpec(spec, ServerPrice.class);
        return createStatsResponse(statisticsResponse);
    }

    private StatsResponse<ServerPriceResponse> createStatsResponse(Map<String, Object> statisticsResponse) {
        Objects.requireNonNull(statisticsResponse, "Statistics response cannot be null");
        if (statisticsResponse.isEmpty()) {
            throw new StatisticsNotFoundException("No server price statistics found");
        }
        BigDecimal average = BigDecimal.valueOf((double) statisticsResponse.get("avg"))
                .setScale(6, RoundingMode.HALF_UP)
                .stripTrailingZeros();

        ServerPrice maxPrice = (ServerPrice) statisticsResponse.get("max");
        ServerPrice minPrice = (ServerPrice) statisticsResponse.get("min");

        ServerPriceResponse min = serverPriceMapper.toPriceResponse(minPrice);
        ServerPriceResponse max = serverPriceMapper.toPriceResponse(maxPrice);


        long count = (long) statisticsResponse.get("count");

        return StatsResponse.<ServerPriceResponse>builder()
                .average(average)
                .maximum(max)
                .minimum(min)
                .count(count)
                .build();
    }

    public StatsResponse<ServerPriceResponse> getStatsForServer(String serverIdentifier) {
        validateNonNullOrEmptyString(serverIdentifier, "Server identifier cannot be null or empty");
        Specification<ServerPrice> spec = searchSpecification.getJoinSpecForServerIdentifier(serverIdentifier);
        Map<String, Object> statisticsResponse = statisticsDao.getStatsForSpec(spec, ServerPrice.class);
        return createStatsResponse(statisticsResponse);
    }

    public StatsResponse<ServerPriceResponse> getStatsForAll(int timeRangeInDays) {
        Specification<ServerPrice> spec = searchSpecification.getSpecForTimeRange(timeRangeInDays);
        Map<String, Object> statisticsResponse = statisticsDao.getStatsForSpec(spec, ServerPrice.class);
        return createStatsResponse(statisticsResponse);
    }
}
