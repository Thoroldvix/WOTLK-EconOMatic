package com.thoroldvix.pricepal.goldprice;

import com.thoroldvix.pricepal.shared.RequestDto;
import com.thoroldvix.pricepal.shared.SearchSpecification;
import com.thoroldvix.pricepal.shared.StatsResponse;
import com.thoroldvix.pricepal.error.StatisticsNotFoundException;
import com.thoroldvix.pricepal.shared.StatisticsDao;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Objects;

import static com.thoroldvix.pricepal.shared.ValidationUtils.validateNonNullOrEmptyString;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GoldPriceStatsService {
    private final StatisticsDao<GoldPrice> statisticsDao;
    private final SearchSpecification<GoldPrice> searchSpecification;
    private final GoldPriceMapper goldPriceMapper;

    public StatsResponse<GoldPriceResponse> getStatsForSearch(RequestDto requestDto) {
        Objects.requireNonNull(requestDto, "RequestDto cannot be null");
        Specification<GoldPrice> spec;
        spec = searchSpecification.createSearchSpecification(requestDto.searchCriteria(), requestDto.globalOperator());

        Map<String, Object> statisticsResponse = statisticsDao.getStatsForSpec(spec, GoldPrice.class);
        return createStatsResponse(statisticsResponse);
    }

    private StatsResponse<GoldPriceResponse> createStatsResponse(Map<String, Object> statisticsResponse) {
        Objects.requireNonNull(statisticsResponse, "Statistics response cannot be null");
        if (statisticsResponse.isEmpty()) {
            throw new StatisticsNotFoundException("No server price statistics found");
        }
        BigDecimal average = BigDecimal.valueOf((double) statisticsResponse.get("avg"))
                .setScale(6, RoundingMode.HALF_UP)
                .stripTrailingZeros();

        GoldPrice maxPrice = (GoldPrice) statisticsResponse.get("max");
        GoldPrice minPrice = (GoldPrice) statisticsResponse.get("min");

        GoldPriceResponse min = goldPriceMapper.toPriceResponse(minPrice);
        GoldPriceResponse max = goldPriceMapper.toPriceResponse(maxPrice);


        long count = (long) statisticsResponse.get("count");

        return StatsResponse.<GoldPriceResponse>builder()
                .average(average)
                .maximum(max)
                .minimum(min)
                .count(count)
                .build();
    }

    public StatsResponse<GoldPriceResponse> getStatsForServer(String serverIdentifier) {
        validateNonNullOrEmptyString(serverIdentifier, "Server identifier cannot be null or empty");
        Specification<GoldPrice> spec = searchSpecification.getJoinSpecForServerIdentifier(serverIdentifier);
        Map<String, Object> statisticsResponse = statisticsDao.getStatsForSpec(spec, GoldPrice.class);
        return createStatsResponse(statisticsResponse);
    }

    public StatsResponse<GoldPriceResponse> getStatsForAll(int timeRangeInDays) {
        Specification<GoldPrice> spec = searchSpecification.getSpecForTimeRange(timeRangeInDays);
        Map<String, Object> statisticsResponse = statisticsDao.getStatsForSpec(spec, GoldPrice.class);
        return createStatsResponse(statisticsResponse);
    }
}
