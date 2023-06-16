package com.thoroldvix.pricepal.goldprice;

import com.thoroldvix.pricepal.shared.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Objects;

import static com.thoroldvix.pricepal.shared.ServerSearchCriteriaBuilder.getJoinCriteria;
import static com.thoroldvix.pricepal.shared.ValidationUtils.validateNonNullOrEmptyString;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GoldPriceStatsService {
    private final StatisticsRepository<GoldPrice> statisticsRepositoryImpl;
    private final SearchSpecification<GoldPrice> searchSpecification;
    private final GoldPriceMapper goldPriceMapper;

    public StatsResponse<GoldPriceResponse> getForSearch(RequestDto requestDto) {
        Objects.requireNonNull(requestDto, "RequestDto cannot be null");
        Specification<GoldPrice> spec;
        spec = searchSpecification.createSearchSpecification(requestDto.globalOperator(), requestDto.searchCriteria());

        Map<String, Object> statisticsResponse = statisticsRepositoryImpl.getStats(spec, GoldPrice.class);
        return createStatsResponse(statisticsResponse);
    }

    private StatsResponse<GoldPriceResponse> createStatsResponse(Map<String, Object> statisticsResponse) {
        Objects.requireNonNull(statisticsResponse, "Statistics response cannot be null");
        if (statisticsResponse.isEmpty()) {
            throw new IllegalArgumentException("Statistics response cannot be empty");
        }
        BigDecimal average = BigDecimal.valueOf((double) statisticsResponse.get("avg"))
                .setScale(6, RoundingMode.HALF_UP)
                .stripTrailingZeros();

        GoldPrice maxPrice = (GoldPrice) statisticsResponse.get("max");
        GoldPrice minPrice = (GoldPrice) statisticsResponse.get("min");

        GoldPriceResponse min = goldPriceMapper.toGoldPriceResponse(minPrice);
        GoldPriceResponse max = goldPriceMapper.toGoldPriceResponse(maxPrice);


        long count = (long) statisticsResponse.get("count");

        return StatsResponse.<GoldPriceResponse>builder()
                .average(average)
                .maximum(max)
                .minimum(min)
                .count(count)
                .build();
    }

    public StatsResponse<GoldPriceResponse> getForServer(String serverIdentifier) {
        validateNonNullOrEmptyString(serverIdentifier, "Server identifier cannot be null or empty");
        SearchCriteria joinCriteria = getJoinCriteria(serverIdentifier);
        Specification<GoldPrice> spec = searchSpecification.createSearchSpecification(RequestDto.GlobalOperator.AND, joinCriteria);
        Map<String, Object> statisticsResponse = statisticsRepositoryImpl.getStats(spec, GoldPrice.class);
        return createStatsResponse(statisticsResponse);
    }

    public StatsResponse<GoldPriceResponse> getForAll(int timeRangeInDays) {
        Specification<GoldPrice> spec = searchSpecification.getSpecForTimeRange(timeRangeInDays);
        Map<String, Object> statisticsResponse = statisticsRepositoryImpl.getStats(spec, GoldPrice.class);
        return createStatsResponse(statisticsResponse);
    }
}
