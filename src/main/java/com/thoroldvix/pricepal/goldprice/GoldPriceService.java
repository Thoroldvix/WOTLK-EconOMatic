package com.thoroldvix.pricepal.goldprice;

import com.thoroldvix.pricepal.shared.RequestDto;
import com.thoroldvix.pricepal.shared.SearchSpecification;
import com.thoroldvix.pricepal.population.PopulationNotFoundException;
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
public class GoldPriceService {

    private final GoldPriceRepository goldPriceRepository;
    private final GoldPriceMapper goldPriceMapper;
    private final SearchSpecification<GoldPrice> searchSpecification;

    public List<GoldPriceResponse> getAllPrices(int timeRangeInDays, Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable must not be null");
        Specification<GoldPrice> spec = searchSpecification.getSpecForTimeRange(timeRangeInDays);
        List<GoldPrice> prices = goldPriceRepository.findAll(spec, pageable).getContent();
        validateListNotNullOrEmpty(prices,
                () -> new GoldPriceNotFoundException("No prices found"));
        return goldPriceMapper.toPriceResponseList(prices);
    }

    public List<GoldPriceResponse> getAllPricesRecent(Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable must not be null");
        List<GoldPrice> prices = goldPriceRepository.findAllRecent(pageable).getContent();
        validateListNotNullOrEmpty(prices,
                () -> new GoldPriceNotFoundException("No prices found"));
        return goldPriceMapper.toPriceResponseList(prices);
    }

    public List<GoldPriceResponse> searchForPrices(RequestDto requestDto,
                                                   Pageable pageable) {
        Objects.requireNonNull(requestDto, "RequestDto must not be null");
        Objects.requireNonNull(pageable, "Pageable must not be null");
        Specification<GoldPrice> spec = searchSpecification.createSearchSpecification(requestDto.searchCriteria(), requestDto.globalOperator());
        List<GoldPrice> prices = goldPriceRepository.findAll(spec, pageable).getContent();
        validateListNotNullOrEmpty(prices,
                () -> new GoldPriceNotFoundException("No prices found"));
        return goldPriceMapper.toPriceResponseList(prices);
    }
    public List<GoldPriceResponse> getPricesForServer(String serverIdentifier,
                                                      Pageable pageable) {
        validateNonNullOrEmptyString(serverIdentifier, "Server identifier cannot be null or empty");
        Specification<GoldPrice> spec = searchSpecification.getJoinSpecForServerIdentifier(serverIdentifier);
        List<GoldPrice> prices = goldPriceRepository.findAll(spec, pageable).getContent();
        validateListNotNullOrEmpty(prices,
                () -> new PopulationNotFoundException("No prices found for server identifier: " + serverIdentifier));
        return goldPriceMapper.toPriceResponseList(prices);
    }
    public GoldPriceResponse getRecentForServer(String serverIdentifier) {
        validateNonNullOrEmptyString(serverIdentifier, "Server identifier cannot be null or empty");
        GoldPrice goldPrice;
        if (isNumber(serverIdentifier)) {
            int serverId = Integer.parseInt(serverIdentifier);
            goldPrice = goldPriceRepository.findRecentByServerId(serverId)
                    .orElseThrow(() -> new GoldPriceNotFoundException("No recent price found for server identifier: " + serverIdentifier));
        } else {
            goldPrice = goldPriceRepository.findRecentByServerUniqueName(serverIdentifier)
                    .orElseThrow(() -> new GoldPriceNotFoundException("No recent price found for server identifier: " + serverIdentifier));
        }
        return goldPriceMapper.toPriceResponse(goldPrice);
    }

    @Transactional
    public void saveAllPrices(List<GoldPrice> pricesToSave) {
        validateListNotNullOrEmpty(pricesToSave,
                () -> new IllegalArgumentException("Prices cannot be null or empty"));
        goldPriceRepository.saveAll(pricesToSave);
    }
}