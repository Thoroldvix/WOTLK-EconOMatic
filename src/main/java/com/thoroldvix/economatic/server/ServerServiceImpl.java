package com.thoroldvix.economatic.server;

import com.thoroldvix.economatic.shared.SearchRequest;
import com.thoroldvix.economatic.shared.SearchSpecification;
import com.thoroldvix.economatic.shared.StringEnumConverter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

import static com.thoroldvix.economatic.server.ServerErrorMessages.*;
import static com.thoroldvix.economatic.shared.ValidationUtils.validateCollectionNotEmpty;

@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServerServiceImpl implements ServerService {

    private final ServerRepository serverRepository;
    private final ServerSummaryMapper serverSummaryMapper;
    private final ServerMapper serverMapper;
    private final SearchSpecification<Server> searchSpecification;


    @Override
    @Cacheable("server-cache")
    public ServerResponse getServer(
            @NotEmpty(message = SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY)
            String serverIdentifier) {
        Optional<Server> server = findServer(serverIdentifier);
        return server.map(serverMapper::toResponse)
                .orElseThrow(() -> new ServerNotFoundException("No server found for identifier: " + serverIdentifier));
    }

    @Override
    public List<ServerResponse> search(@Valid @NotNull(message = "Search request cannot be null") SearchRequest searchRequest) {
        Specification<Server> spec =
                searchSpecification.create(searchRequest.globalOperator(), searchRequest.searchCriteria());
        List<Server> servers = serverRepository.findAll(spec);
        validateCollectionNotEmpty(servers, () -> new ServerNotFoundException("No servers found for search request"));
        return serverMapper.toResponseList(servers);
    }

    @Override
    @Cacheable("server-cache")
    public List<ServerResponse> getAll() {
        List<Server> servers = serverRepository.findAll();
        validateCollectionNotEmpty(servers, () -> new ServerNotFoundException("No servers found"));
        return serverMapper.toResponseList(servers);
    }


    @Override
    public ServerSummaryResponse getSummary() {
        ServerSummaryProjection summaryProjection = serverRepository.getSummary();
        return serverSummaryMapper.toResponse(summaryProjection);
    }

    @Override
    public List<ServerResponse> getAllForRegion(
            @NotEmpty(message = REGION_NAME_CANNOT_BE_NULL_OR_EMPTY)
            String regionName) {
        List<Server> servers = findAllByRegion(regionName);
        validateCollectionNotEmpty(servers, () -> new ServerNotFoundException("No servers found for region: " + regionName));
        return serverMapper.toResponseList(servers);
    }

    @Override
    public List<ServerResponse> getAllForFaction(
            @NotEmpty(message = FACTION_NAME_CANNOT_BE_NULL_OR_EMPTY)
            String factionName) {
        List<Server> servers = findAllByFaction(factionName);
        validateCollectionNotEmpty(servers, () -> new ServerNotFoundException("No servers found for faction: " + factionName));
        return serverMapper.toResponseList(servers);
    }

    private List<Server> findAllByRegion(String regionName) {
        Region region = StringEnumConverter.fromString(regionName, Region.class);
        return serverRepository.findAllByRegion(region);
    }

    private List<Server> findAllByFaction(String factionName) {
        Faction faction = StringEnumConverter.fromString(factionName, Faction.class);
        return serverRepository.findAllByFaction(faction);
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
