package com.thoroldvix.pricepal.goldprice;

import com.thoroldvix.pricepal.population.PopulationNotFoundException;
import com.thoroldvix.pricepal.server.ServerService;
import com.thoroldvix.pricepal.shared.SearchRequest;
import com.thoroldvix.pricepal.shared.SearchSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.thoroldvix.pricepal.shared.ValidationUtils.validateCollectionNotNullOrEmpty;
import static com.thoroldvix.pricepal.shared.ValidationUtils.validateStringNonNullOrEmpty;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoldPriceService {

    private final ServerService serverServiceImpl;
    private final GoldPriceRepository goldPriceRepository;
    private final GoldPriceMapper goldPriceMapper;
    private final SearchSpecification<GoldPrice> searchSpecification;

    public List<GoldPriceResponse> getAll(int timeRange, Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable must not be null");
        List<GoldPrice> prices = findForTimeRange(timeRange, pageable);
        validateCollectionNotNullOrEmpty(prices,
                () -> new GoldPriceNotFoundException("No prices found for time range: " + timeRange));
        return goldPriceMapper.toResponseListWithServerNames(prices);
    }

    public List<GoldPriceResponse> getAllRecent(Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable must not be null");
        List<GoldPrice> prices = goldPriceRepository.findAllRecent(pageable).getContent();
        validateCollectionNotNullOrEmpty(prices,
                () -> new GoldPriceNotFoundException("No prices found"));
        return goldPriceMapper.toResponseListWithServerNames(prices);
    }

    public List<GoldPriceResponse> search(SearchRequest searchRequest,
                                          Pageable pageable) {
        Objects.requireNonNull(searchRequest, "SearchRequest must not be null");
        Objects.requireNonNull(pageable, "Pageable must not be null");
        List<GoldPrice> prices = findAllForSearch(searchRequest, pageable);
        validateCollectionNotNullOrEmpty(prices,
                () -> new GoldPriceNotFoundException("No prices found"));
        return goldPriceMapper.toResponseListWithServerNames(prices);
    }

    public GoldPriceServerResponse getAllForServer(String serverIdentifier,
                                                   Pageable pageable) {
        validateStringNonNullOrEmpty(serverIdentifier, "Server identifier cannot be null or empty");
        Objects.requireNonNull(pageable, "Pageable must not be null");

        List<GoldPrice> prices = findAllForServer(serverIdentifier, pageable);
        validateCollectionNotNullOrEmpty(prices,
                () -> new PopulationNotFoundException("No prices found for server identifier: " + serverIdentifier));

        return getGoldPriceServerResponse(prices);
    }

    private List<GoldPrice> findAllForServer(String serverIdentifier, Pageable pageable) {
        int serverId = serverServiceImpl.getServer(serverIdentifier).id();
        return goldPriceRepository.findAllForServer(serverId, pageable).getContent();
    }


    public GoldPriceResponse getRecentForServer(String serverIdentifier) {
        validateStringNonNullOrEmpty(serverIdentifier, "Server identifier cannot be null or empty");
        Optional<GoldPrice> goldPrice = findRecentForServer(serverIdentifier);
        return goldPrice.map(goldPriceMapper::toResponseWithServerName)
                .orElseThrow(() -> new GoldPriceNotFoundException("No recent price found for server identifier: " + serverIdentifier));
    }

    @Transactional
    public void saveAll(List<GoldPrice> pricesToSave) {
        validateCollectionNotNullOrEmpty(pricesToSave,
                () -> new IllegalArgumentException("Prices cannot be null or empty"));
        goldPriceRepository.saveAll(pricesToSave);
    }

    private List<GoldPrice> findAllForSearch(SearchRequest searchRequest, Pageable pageable) {
        Specification<GoldPrice> spec = searchSpecification.create(searchRequest.globalOperator(), searchRequest.searchCriteria());
        return goldPriceRepository.findAll(spec, pageable).getContent();
    }

    private GoldPriceServerResponse getGoldPriceServerResponse(List<GoldPrice> prices) {
        String serverName = prices.get(0).getServer().getName();
        List<GoldPriceResponse> priceResponses = goldPriceMapper.toResponseList(prices);
        return GoldPriceServerResponse.builder()
                .server(serverName)
                .prices(priceResponses)
                .build();
    }

    private Optional<GoldPrice> findRecentForServer(String serverIdentifier) {
        int serverId = serverServiceImpl.getServer(serverIdentifier).id();
        return goldPriceRepository.findRecentForServer(serverId);
    }

    private List<GoldPrice> findForTimeRange(int timeRange, Pageable pageable) {
        LocalDateTime start = LocalDateTime.now().minusDays(timeRange);
        LocalDateTime end = LocalDateTime.now();
        return goldPriceRepository.findAllForTimeRange(start, end, pageable).getContent();
    }
}