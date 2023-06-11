package com.thoroldvix.pricepal.server.service;

import com.thoroldvix.pricepal.common.dto.RequestDto;
import com.thoroldvix.pricepal.common.service.SearchSpecification;
import com.thoroldvix.pricepal.server.dto.ServerPriceResponse;
import com.thoroldvix.pricepal.server.entity.ServerPrice;
import com.thoroldvix.pricepal.server.error.PopulationNotFoundException;
import com.thoroldvix.pricepal.server.error.ServerPriceNotFoundException;
import com.thoroldvix.pricepal.server.repository.ServerPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.thoroldvix.pricepal.common.util.ValidationUtils.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServerPriceService {

    private final ServerPriceRepository serverPriceRepository;
    private final ServerPriceMapper serverPriceMapper;
    private final SearchSpecification<ServerPrice> searchSpecification;


    public List<ServerPriceResponse> getAllPrices(int timeRangeInDays, Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable must not be null");
        Specification<ServerPrice> spec = searchSpecification.getSpecForTimeRange(timeRangeInDays);
        List<ServerPrice> prices = serverPriceRepository.findAll(spec, pageable).getContent();
        validateListNotNullOrEmpty(prices,
                () -> new ServerPriceNotFoundException("No prices found"));
        return serverPriceMapper.toPriceResponseList(prices);
    }

    public List<ServerPriceResponse> getAllPricesRecent(Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable must not be null");
        List<ServerPrice> prices = serverPriceRepository.findAllRecent(pageable).getContent();
        validateListNotNullOrEmpty(prices,
                () -> new ServerPriceNotFoundException("No prices found"));
        return serverPriceMapper.toPriceResponseList(prices);
    }

    public List<ServerPriceResponse> searchForPrices(RequestDto requestDto,
                                                     Pageable pageable) {
        Objects.requireNonNull(requestDto, "RequestDto must not be null");
        Objects.requireNonNull(pageable, "Pageable must not be null");
        Specification<ServerPrice> spec = searchSpecification.createSearchSpecification(requestDto.searchCriteria(), requestDto.globalOperator());
        List<ServerPrice> prices = serverPriceRepository.findAll(spec, pageable).getContent();
        validateListNotNullOrEmpty(prices,
                () -> new ServerPriceNotFoundException("No prices found"));
        return serverPriceMapper.toPriceResponseList(prices);
    }
    public List<ServerPriceResponse> getPricesForServer(String serverIdentifier,
                                                        Pageable pageable) {
        validateNonNullOrEmptyString(serverIdentifier, "Server identifier cannot be null or empty");
        Specification<ServerPrice> spec = searchSpecification.getJoinSpecForServerIdentifier(serverIdentifier);
        List<ServerPrice> prices = serverPriceRepository.findAll(spec, pageable).getContent();
        validateListNotNullOrEmpty(prices,
                () -> new PopulationNotFoundException("No prices found for server identifier: " + serverIdentifier));
        return serverPriceMapper.toPriceResponseList(prices);
    }
    public ServerPriceResponse getRecentForServer(String serverIdentifier) {
        validateNonNullOrEmptyString(serverIdentifier, "Server identifier cannot be null or empty");
        ServerPrice serverPrice;
        if (isNumber(serverIdentifier)) {
            int serverId = Integer.parseInt(serverIdentifier);
            serverPrice = serverPriceRepository.findRecentByServerId(serverId)
                    .orElseThrow(() -> new ServerPriceNotFoundException("No recent price found for server identifier: " + serverIdentifier));
        } else {
            serverPrice = serverPriceRepository.findRecentByServerUniqueName(serverIdentifier)
                    .orElseThrow(() -> new ServerPriceNotFoundException("No recent price found for server identifier: " + serverIdentifier));
        }
        return serverPriceMapper.toPriceResponse(serverPrice);
    }

    @Transactional
    public void saveAllPrices(List<ServerPrice> pricesToSave) {
        validateListNotNullOrEmpty(pricesToSave,
                () -> new IllegalArgumentException("Prices cannot be null or empty"));
        serverPriceRepository.saveAll(pricesToSave);
    }
}