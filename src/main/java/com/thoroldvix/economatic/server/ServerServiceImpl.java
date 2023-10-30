package com.thoroldvix.economatic.server;

import com.thoroldvix.economatic.common.util.StringEnumConverter;
import com.thoroldvix.economatic.error.ErrorMessages;
import com.thoroldvix.economatic.search.SearchRequest;
import com.thoroldvix.economatic.search.SpecificationBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.thoroldvix.economatic.common.util.ValidationUtils.notEmpty;
import static com.thoroldvix.economatic.error.ErrorMessages.*;
import static java.util.Objects.requireNonNull;

@Service
@Cacheable("server-cache")
@RequiredArgsConstructor
@Transactional(readOnly = true)
class ServerServiceImpl implements ServerService {

    private final ServerRepository serverRepository;
    private final ServerMapper serverMapper;

    @Override
    public ServerResponse getServer(String serverIdentifier) {
        notEmpty(serverIdentifier, SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY.message);

        Optional<Server> server = findServerByIdentifier(serverIdentifier);

        return server.map(serverMapper::toResponse)
                .orElseThrow(() -> new ServerNotFoundException("No server found for identifier: " + serverIdentifier));
    }

    @Override
    public ServerListResponse search(@Valid SearchRequest searchRequest) {
        requireNonNull(searchRequest, ErrorMessages.SEARCH_REQUEST_CANNOT_BE_NULL.message);

        Specification<Server> spec = SpecificationBuilder.from(searchRequest);
        List<Server> servers = serverRepository.findAll(spec);
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
    public ServerListResponse getAllForRegion(String regionName) {
        notEmpty(regionName, REGION_NAME_CANNOT_BE_NULL_OR_EMPTY.message);

        Region region = StringEnumConverter.fromString(regionName, Region.class);
        List<Server> servers = serverRepository.findAllByRegion(region);
        notEmpty(servers, () -> new ServerNotFoundException("No servers found for region: " + regionName));

        return serverMapper.toServerListResponse(servers);
    }

    @Override
    public ServerListResponse getAllForFaction(String factionName) {
        notEmpty(factionName, FACTION_NAME_CANNOT_BE_NULL_OR_EMPTY.message);

        Faction faction = StringEnumConverter.fromString(factionName, Faction.class);
        List<Server> servers = serverRepository.findAllByFaction(faction);
        notEmpty(servers, () -> new ServerNotFoundException("No servers found for faction: " + factionName));

        return serverMapper.toServerListResponse(servers);
    }

    private Optional<Server> findServerByIdentifier(String serverIdentifier) {
        try {
            int serverId = Integer.parseInt(serverIdentifier);
            return serverRepository.findById(serverId);
        } catch (NumberFormatException e) {
            return serverRepository.findByUniqueName(serverIdentifier);
        }
    }
}
