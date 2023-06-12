package com.thoroldvix.pricepal.server.service;

import com.thoroldvix.pricepal.common.dto.RequestDto;
import com.thoroldvix.pricepal.common.service.SearchSpecification;
import com.thoroldvix.pricepal.server.dto.ServerResponse;
import com.thoroldvix.pricepal.server.entity.Region;
import com.thoroldvix.pricepal.server.entity.Server;
import com.thoroldvix.pricepal.server.error.ServerNotFoundException;
import com.thoroldvix.pricepal.server.repository.ServerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.thoroldvix.pricepal.common.util.ValidationUtils.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServerService {

    private final ServerRepository serverRepository;
    private final ServerMapper serverMapper;
    private final SearchSpecification<Server> searchSpecification;

    public ServerResponse getServer(String serverIdentifier) {
        if (!hasText(serverIdentifier)) {
            throw new IllegalArgumentException("Server identifier cannot be null or empty");
        }
        Optional<Server> server;
        if (isNumber(serverIdentifier)) {
            server  = serverRepository.findById(Integer.parseInt(serverIdentifier));
        } else {
            server = serverRepository.findByUniqueName(serverIdentifier);
        }
        return server.map(serverMapper::toServerResponse)
                .orElseThrow(() -> new ServerNotFoundException("No server found with for identifier: " + serverIdentifier));
    }

    public List<ServerResponse> searchServers(RequestDto requestDto) {
        Objects.requireNonNull(requestDto, "RequestDto cannot be null");
        Specification<Server> spec =
                searchSpecification.createSearchSpecification(requestDto.searchCriteria(), requestDto.globalOperator());
        List<Server> servers = serverRepository.findAll(spec);
        validateListNotNullOrEmpty(servers, () -> new ServerNotFoundException("No servers found"));
        return serverMapper.toServerResponseList(servers);
    }

    public List<ServerResponse> getAllServers() {
        List<Server> servers = serverRepository.findAll();
        validateListNotNullOrEmpty(servers, () -> new ServerNotFoundException("No servers found"));
        return serverMapper.toServerResponseList(servers);
    }

    public List<ServerResponse> getAllServersForRegion(Region region) {
        Objects.requireNonNull(region, "Region cannot be null");
        List<Server> servers = serverRepository.findAllByRegion(region);
        validateListNotNullOrEmpty(servers, () -> new ServerNotFoundException("No servers found for region: " + region.name()));
        return serverMapper.toServerResponseList(servers);
    }
}
