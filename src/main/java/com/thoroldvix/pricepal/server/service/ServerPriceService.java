package com.thoroldvix.pricepal.server.service;

import com.thoroldvix.pricepal.common.FiltersSpecification;
import com.thoroldvix.pricepal.common.RequestDto;
import com.thoroldvix.pricepal.common.ValidationUtils;
import com.thoroldvix.pricepal.server.dto.ServerPriceResponse;
import com.thoroldvix.pricepal.server.dto.StatisticsResponse;
import com.thoroldvix.pricepal.server.entity.Server;
import com.thoroldvix.pricepal.server.entity.ServerPrice;
import com.thoroldvix.pricepal.server.error.ServerPriceNotFoundException;
import com.thoroldvix.pricepal.server.repository.ServerPriceRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ServerPriceService {
    @PersistenceContext
    private final EntityManager entityManager;
    private final ServerPriceRepository serverPriceRepository;
    private final ServerPriceMapper serverPriceMapper;
    private final FiltersSpecification<ServerPrice> filtersSpecification;


    public List<ServerPriceResponse> getAllPrices(Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable must not be null");
        List<ServerPrice> prices = serverPriceRepository.findAll(pageable).getContent();
        ValidationUtils.validateListNotEmpty(prices,
                () -> new ServerPriceNotFoundException("No prices found"));
        return serverPriceMapper.toPriceResponseList(prices);
    }

    public List<ServerPriceResponse> searchForPrices(RequestDto requestDto, Pageable pageable) {
        Objects.requireNonNull(requestDto, "RequestDto must not be null");
        Objects.requireNonNull(pageable, "Pageable must not be null");
        Specification<ServerPrice> searchSpecification =
                filtersSpecification.getSearchSpecification(requestDto.searchCriteria(), requestDto.globalOperator());
        List<ServerPrice> prices = serverPriceRepository.findAll(searchSpecification, pageable).getContent();
        ValidationUtils.validateListNotEmpty(prices,
                () -> new ServerPriceNotFoundException("No prices found"));
        return serverPriceMapper.toPriceResponseList(prices);
    }

    public List<ServerPriceResponse> getPricesForServer(String serverName, Pageable pageable) {
        ValidationUtils.validateNonEmptyString(serverName, "Server name cannot be null or empty");
        Objects.requireNonNull(pageable, "Pageable must not be null");
        List<ServerPrice> prices = serverPriceRepository.findAllForServerUniqueName(serverName, pageable).getContent();
        ValidationUtils.validateListNotEmpty(prices,
                () -> new ServerPriceNotFoundException("No prices found for server name: " + serverName));
        return serverPriceMapper.toPriceResponseList(prices);
    }

    public StatisticsResponse getStatisticsForSearch(RequestDto requestDto) {
        Objects.requireNonNull(requestDto, "RequestDto must not be null");
        List<ServerPriceResponse> prices = searchForPrices(requestDto, Pageable.unpaged());
        return calculateStatistics(prices);
    }

    public StatisticsResponse getStatisticsForServer(String serverName) {
        ValidationUtils.validateNonEmptyString(serverName, "Server name cannot be null or empty");
        List<ServerPriceResponse> prices = getPricesForServer(serverName, Pageable.unpaged());
        return calculateStatistics(prices);
    }


    @Transactional
    public void savePrice(int serverId, ServerPriceResponse recentPrice) {
        Objects.requireNonNull(recentPrice, "Recent server price must not be null");
        Server server = entityManager.getReference(Server.class, serverId);
        ServerPrice serverPrice = serverPriceMapper.toServerPrice(recentPrice);
        serverPrice.setServer(server);
        serverPriceRepository.save(serverPrice);
    }

    private ServerPriceResponse calculatePrice(List<ServerPriceResponse> prices, Function<Stream<BigDecimal>, Optional<BigDecimal>> operation) {
        ValidationUtils.validateListNotEmpty(prices, () -> new IllegalArgumentException("Prices cannot be null or empty"));
        BigDecimal price = operation.apply(prices.stream().map(ServerPriceResponse::price))
                .orElseThrow(() -> new ServerPriceNotFoundException("Cannot find price"));
        return findPriceResponseForPriceInList(prices, price);
    }

    private BigDecimal calculateAvgPrice(List<ServerPriceResponse> prices) {
        ValidationUtils.validateListNotEmpty(prices, () -> new IllegalArgumentException("Prices cannot be null or empty"));
        return prices.stream()
                .map(ServerPriceResponse::price)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(prices.size()), RoundingMode.HALF_UP)
                .setScale(5, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateMedianPrice(List<ServerPriceResponse> prices) {
        ValidationUtils.validateListNotEmpty(prices, () -> new IllegalArgumentException("Prices cannot be null or empty"));
        List<BigDecimal> sortedPrices = prices.stream()
                .map(ServerPriceResponse::price)
                .sorted()
                .toList();

        int size = sortedPrices.size();
        int middleIndex = size / 2;

        if (size % 2 == 0) {
            return sortedPrices.get(middleIndex)
                    .add(sortedPrices.get(middleIndex - 1))
                    .divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP)
                    .setScale(5, RoundingMode.HALF_UP);
        } else {
            return sortedPrices.get(middleIndex);
        }
    }

    private StatisticsResponse calculateStatistics(List<ServerPriceResponse> prices) {
        ValidationUtils.validateListNotEmpty(prices, () -> new IllegalArgumentException("Prices cannot be null or empty"));
        BigDecimal avgPrice = calculateAvgPrice(prices);
        BigDecimal medianPrice = calculateMedianPrice(prices);
        ServerPriceResponse maxPrice = calculatePrice(prices, stream -> stream.max(BigDecimal::compareTo));
        ServerPriceResponse minPrice = calculatePrice(prices, stream -> stream.min(BigDecimal::compareTo));
        return new StatisticsResponse(avgPrice, medianPrice, minPrice, maxPrice);
    }

    private ServerPriceResponse findPriceResponseForPriceInList(List<ServerPriceResponse> prices, BigDecimal price) {
        Objects.requireNonNull(prices, "Prices cannot be null");
        Objects.requireNonNull(price, "Price value cannot be null");
        return prices.stream()
                .filter(serverPriceResponse -> serverPriceResponse.price().equals(price))
                .findFirst()
                .orElseThrow(() -> new ServerPriceNotFoundException("Cannot find price"));
    }


}