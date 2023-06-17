package com.thoroldvix.pricepal.goldprice;

import com.thoroldvix.pricepal.shared.RequestDto;
import com.thoroldvix.pricepal.shared.SearchCriteria;
import com.thoroldvix.pricepal.shared.SearchSpecification;
import com.thoroldvix.pricepal.population.PopulationNotFoundException;
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
public class GoldPriceService {

    private final GoldPriceRepository goldPriceRepository;
    private final GoldPriceMapper goldPriceMapper;
    private final SearchSpecification<GoldPrice> searchSpecification;


    public List<GoldPriceResponse> getAll(int timeRangeInDays, Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable must not be null");
        Specification<GoldPrice> spec = searchSpecification.getSpecForTimeRange(timeRangeInDays);
        List<GoldPrice> prices = goldPriceRepository.findAll(spec, pageable).getContent();
        validateCollectionNotNullOrEmpty(prices,
                () -> new GoldPriceNotFoundException("No prices found"));
        return goldPriceMapper.toResponseList(prices);
    }

    public List<GoldPriceResponse> getAllRecent(Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable must not be null");
        List<GoldPrice> prices = goldPriceRepository.findAllRecent(pageable).getContent();
        validateCollectionNotNullOrEmpty(prices,
                () -> new GoldPriceNotFoundException("No prices found"));
        return goldPriceMapper.toResponseList(prices);
    }

    public List<GoldPriceResponse> search(RequestDto requestDto,
                                          Pageable pageable) {
        Objects.requireNonNull(requestDto, "RequestDto must not be null");
        Objects.requireNonNull(pageable, "Pageable must not be null");
        Specification<GoldPrice> spec = searchSpecification.createSearchSpecification(requestDto.globalOperator(), requestDto.searchCriteria());
        List<GoldPrice> prices = goldPriceRepository.findAll(spec, pageable).getContent();
        validateCollectionNotNullOrEmpty(prices,
                () -> new GoldPriceNotFoundException("No prices found"));
        return goldPriceMapper.toResponseList(prices);
    }

    public List<GoldPriceResponse> getForServer(String serverIdentifier,
                                                Pageable pageable) {
        validateStringNonNullOrEmpty(serverIdentifier, "Server identifier cannot be null or empty");
        SearchCriteria serverCriteria = getJoinCriteria(serverIdentifier);
        Specification<GoldPrice> spec = searchSpecification.createSearchSpecification(RequestDto.GlobalOperator.AND, serverCriteria);
        List<GoldPrice> prices = goldPriceRepository.findAll(spec, pageable).getContent();
        validateCollectionNotNullOrEmpty(prices,
                () -> new PopulationNotFoundException("No prices found for server identifier: " + serverIdentifier));
        return goldPriceMapper.toResponseList(prices);
    }

    public GoldPriceResponse getRecentForServer(String serverIdentifier) {
        validateStringNonNullOrEmpty(serverIdentifier, "Server identifier cannot be null or empty");
        Optional<GoldPrice> goldPrice = findRecentForServer(serverIdentifier);
        return goldPrice.map(goldPriceMapper::toResponse)
                .orElseThrow(() -> new GoldPriceNotFoundException("No recent price found for server identifier: " + serverIdentifier));
    }

    private Optional<GoldPrice> findRecentForServer(String serverIdentifier) {
        try {
            int serverId = Integer.parseInt(serverIdentifier);
            return goldPriceRepository.findRecentByServerId(serverId);
        } catch (NumberFormatException e) {
            return goldPriceRepository.findRecentByServerUniqueName(serverIdentifier);
        }
    }

    @Transactional
    public void saveAll(List<GoldPrice> pricesToSave) {
        validateCollectionNotNullOrEmpty(pricesToSave,
                () -> new IllegalArgumentException("Prices cannot be null or empty"));
        goldPriceRepository.saveAll(pricesToSave);
    }
}