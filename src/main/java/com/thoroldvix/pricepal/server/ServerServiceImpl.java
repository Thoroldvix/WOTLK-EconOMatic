package com.thoroldvix.pricepal.server;

import com.thoroldvix.pricepal.shared.RequestDto;
import com.thoroldvix.pricepal.shared.SearchSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.thoroldvix.pricepal.shared.ValidationUtils.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServerServiceImpl implements ServerService {

    private final ServerRepository serverRepository;
    private final ServerMapper serverMapper;
    private final SearchSpecification<Server> searchSpecification;


    @Override
    public ServerResponse getServer(String serverIdentifier) {
        validateStringNonNullOrEmpty(serverIdentifier, "Server identifier cannot be null or empty");
        Optional<Server> server = findServer(serverIdentifier);
        return server.map(serverMapper::toResponse)
                .orElseThrow(() -> new ServerNotFoundException("No server found for identifier: " + serverIdentifier));
    }

    @Override
    public List<ServerResponse> search(RequestDto requestDto) {
        Objects.requireNonNull(requestDto, "RequestDto cannot be null");
        Specification<Server> spec =
                searchSpecification.createSearchSpecification(requestDto.globalOperator(), requestDto.searchCriteria());
        List<Server> servers = serverRepository.findAll(spec);
        validateCollectionNotNullOrEmpty(servers, () -> new ServerNotFoundException("No servers found"));
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
        Objects.requireNonNull(region, "Region cannot be null");
        List<Server> servers = serverRepository.findAllByRegion(region);
        validateCollectionNotNullOrEmpty(servers, () -> new ServerNotFoundException("No servers found for region: " + region.name()));
        return serverMapper.toResponseList(servers);
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
