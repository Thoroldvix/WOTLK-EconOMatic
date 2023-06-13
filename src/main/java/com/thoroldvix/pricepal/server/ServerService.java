package com.thoroldvix.pricepal.server;

import com.thoroldvix.pricepal.shared.RequestDto;
import com.thoroldvix.pricepal.shared.SearchSpecification;
import lombok.RequiredArgsConstructor;
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
