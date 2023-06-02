package com.thoroldvix.pricepal.server.service;

import com.thoroldvix.pricepal.common.FiltersSpecification;
import com.thoroldvix.pricepal.common.RequestDto;
import com.thoroldvix.pricepal.common.ValidationUtils;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServerService {

    private final ServerRepository serverRepository;
    private final ServerMapper serverMapper;
    private final FiltersSpecification<Server> filtersSpecification;

    public Server getServer(int id) {
        ValidationUtils.validatePositiveInt(id, "Server id must be a positive integer");
        return serverRepository.findById(id)
                .orElseThrow(() -> new ServerNotFoundException("No server found for ID: " + id));
    }

    public Server getServer(String uniqueServerName) {
        ValidationUtils.validateNonEmptyString(uniqueServerName, "Server name cannot be null or empty");
        return serverRepository.findByUniqueName(uniqueServerName)
                .orElseThrow(() -> new ServerNotFoundException("No server found with name: " + uniqueServerName));
    }

    public ServerResponse getServerResponse(int id) {
        ValidationUtils.validatePositiveInt(id, "Server id must be a positive integer");
        return serverRepository.findById(id)
                .map(serverMapper::toServerResponse)
                .orElseThrow(() -> new ServerNotFoundException("No server found for ID: " + id));

    }


    public ServerResponse getServerResponse(String uniqueServerName) {
        ValidationUtils.validateNonEmptyString(uniqueServerName, "Server name cannot be null or empty");
        return serverRepository.findByUniqueName(uniqueServerName)
                .map(serverMapper::toServerResponse)
                .orElseThrow(() -> new ServerNotFoundException("No server found with name: " + uniqueServerName));
    }


    public List<ServerResponse> searchServers(RequestDto requestDto) {
        Specification<Server> specification =
                filtersSpecification.getSearchSpecification(requestDto.searchCriteria(), requestDto.globalOperator());
        List<Server> servers = serverRepository.findAll(specification);
        ValidationUtils.validateListNotEmpty(servers, () -> new ServerNotFoundException("No servers found"));
        return serverMapper.toServerResponseList(servers);
    }

    public List<ServerResponse> getAllServers() {
        List<Server> servers = serverRepository.findAll();
        ValidationUtils.validateListNotEmpty(servers, () -> new ServerNotFoundException("No servers found"));
        return serverMapper.toServerResponseList(servers);
    }

    public List<ServerResponse> getAllServersForRegion(Region region) {
        Objects.requireNonNull(region, "Region cannot be null");
        List<Server> servers = serverRepository.findAllByRegion(region);
        ValidationUtils.validateListNotEmpty(servers, () -> new ServerNotFoundException("No servers found for region: " + region.name()));
        return serverMapper.toServerResponseList(servers);
    }
}
