package com.thoroldvix.pricepal.server;

import com.thoroldvix.pricepal.shared.SearchRequest;
import com.thoroldvix.pricepal.shared.SearchSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.thoroldvix.pricepal.server.ServerErrorMessages.REGION_CANNOT_BE_NULL;
import static com.thoroldvix.pricepal.server.ServerErrorMessages.SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY;
import static com.thoroldvix.pricepal.shared.ErrorMessages.SEARCH_REQUEST_CANNOT_BE_NULL;
import static com.thoroldvix.pricepal.shared.ValidationUtils.validateCollectionNotNullOrEmpty;
import static com.thoroldvix.pricepal.shared.ValidationUtils.validateStringNonNullOrEmpty;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServerServiceImpl implements ServerService {

    private final ServerRepository serverRepository;
    private final ServerSummaryMapper serverSummaryMapper;
    private final ServerMapper serverMapper;
    private final SearchSpecification<Server> searchSpecification;


    @Override
    public ServerResponse getServer(String serverIdentifier) {
        validateStringNonNullOrEmpty(serverIdentifier, SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY);
        Optional<Server> server = findServer(serverIdentifier);
        return server.map(serverMapper::toResponse)
                .orElseThrow(() -> new ServerNotFoundException("No server found for identifier: " + serverIdentifier));
    }

    @Override
    public List<ServerResponse> search(SearchRequest searchRequest) {
        Objects.requireNonNull(searchRequest, SEARCH_REQUEST_CANNOT_BE_NULL);
        Specification<Server> spec =
                searchSpecification.create(searchRequest.globalOperator(), searchRequest.searchCriteria());
        List<Server> servers = serverRepository.findAll(spec);
        validateCollectionNotNullOrEmpty(servers, () -> new ServerNotFoundException("No servers found for search request"));
        return serverMapper.toResponseList(servers);
    }

    @Override
    @Cacheable(value= "server-cache")
    public List<ServerResponse> getAll() {
        List<Server> servers = serverRepository.findAll();
        validateCollectionNotNullOrEmpty(servers, () -> new ServerNotFoundException("No servers found"));
        return serverMapper.toResponseList(servers);
    }

    @Override
    public List<ServerResponse> getAllForRegion(Region region) {
        Objects.requireNonNull(region, REGION_CANNOT_BE_NULL);
        List<Server> servers = serverRepository.findAllByRegion(region);
        validateCollectionNotNullOrEmpty(servers, () -> new ServerNotFoundException("No servers found for region: " + region.name()));
        return serverMapper.toResponseList(servers);
    }

    @Override
    public ServerSummaryResponse getSummary() {
        ServerSummaryProjection summaryProjection = serverRepository.getSummary();
        return serverSummaryMapper.toResponse(summaryProjection);
    }


    private Optional<Server> findServer(String serverIdentifier) {
        try {
            int serverId = Integer.parseInt(serverIdentifier);
            return serverRepository.findById(serverId);
        } catch (NumberFormatException e) {
            return serverRepository.findByUniqueName(serverIdentifier);
        }
    }
}
