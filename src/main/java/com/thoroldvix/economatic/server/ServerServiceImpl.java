package com.thoroldvix.economatic.server;

import com.thoroldvix.economatic.error.ErrorMessages;
import com.thoroldvix.economatic.server.dto.ServerListResponse;
import com.thoroldvix.economatic.server.dto.ServerResponse;
import com.thoroldvix.economatic.server.dto.ServerSummaryResponse;
import com.thoroldvix.economatic.shared.dto.SearchRequest;
import com.thoroldvix.economatic.shared.SearchSpecification;
import com.thoroldvix.economatic.shared.StringEnumConverter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

import static com.thoroldvix.economatic.server.ServerErrorMessages.*;
import static com.thoroldvix.economatic.shared.ValidationUtils.notEmpty;
import static java.util.Objects.requireNonNull;

@Service
@Validated
@Cacheable("server-cache")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServerServiceImpl implements ServerService {

    private final ServerRepository serverRepository;
    private final ServerSummaryMapper serverSummaryMapper;
    private final ServerMapper serverMapper;
    private final SearchSpecification<Server> searchSpecification;


    @Override
    public ServerResponse getServer(String serverIdentifier) {
        notEmpty(serverIdentifier, SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY);

        Optional<Server> server = findServer(serverIdentifier);

        return server.map(serverMapper::toResponse)
                .orElseThrow(() -> new ServerNotFoundException("No server found for identifier: " + serverIdentifier));
    }

    @Override
    public ServerListResponse search(@Valid SearchRequest searchRequest) {
        requireNonNull(searchRequest, ErrorMessages.SEARCH_REQUEST_CANNOT_BE_NULL);

        List<Server> servers = findForSearch(searchRequest);
        notEmpty(servers, () -> new ServerNotFoundException("No servers found for search request"));

        return serverMapper.toServerListResponse(servers);
    }


    @Override
    public ServerListResponse getAll() {
        List<Server> servers = serverRepository.findAll();
        notEmpty(servers, () -> new ServerNotFoundException("No servers found"));
        return serverMapper.toServerListResponse(servers);
    }


    @Override
    public ServerSummaryResponse getSummary() {
        ServerSummaryProjection summaryProjection = serverRepository.getSummary();
        return serverSummaryMapper.toResponse(summaryProjection);
    }

    @Override
    public ServerListResponse getAllForRegion(String regionName) {
        notEmpty(regionName, REGION_NAME_CANNOT_BE_NULL_OR_EMPTY);

        List<Server> servers = findAllByRegion(regionName);
        notEmpty(servers, () -> new ServerNotFoundException("No servers found for region: " + regionName));

        return serverMapper.toServerListResponse(servers);
    }

    @Override
    public ServerListResponse getAllForFaction(String factionName) {
        notEmpty(factionName, FACTION_NAME_CANNOT_BE_NULL_OR_EMPTY);

        List<Server> servers = findAllByFaction(factionName);
        notEmpty(servers, () -> new ServerNotFoundException("No servers found for faction: " + factionName));

        return serverMapper.toServerListResponse(servers);
    }

    private List<Server> findForSearch(SearchRequest searchRequest) {
        Specification<Server> spec =
                searchSpecification.create(searchRequest.globalOperator(), searchRequest.searchCriteria());
        return serverRepository.findAll(spec);
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
