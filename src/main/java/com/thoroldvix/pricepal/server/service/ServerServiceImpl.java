package com.thoroldvix.pricepal.server.service;

import com.thoroldvix.pricepal.server.dto.PopulationResponse;
import com.thoroldvix.pricepal.server.dto.ServerResponse;
import com.thoroldvix.pricepal.server.entity.*;
import com.vaadin.flow.router.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServerServiceImpl implements ServerService {

    private final ServerRepository serverRepository;
    private final ServerMapper serverMapper;
    private final ServerPriceMapper serverPriceMapper;

    @Override
    public List<ServerResponse> getAllServers(Pageable pageable) {
        return serverRepository.findAll(pageable).getContent().stream()
                .map(serverMapper::toServerResponse)
                .toList();
    }

    @Override
    public Server getServer(int id) {
        return serverRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No server found for id: " + id));
    }

    @Override
    public List<ServerResponse> getAllServersForRegion(Region region) {
        Objects.requireNonNull(region, "Region cannot be null");
        return getAllServersForRegion(List.of(region));
    }

    @Override
    public List<ServerResponse> getAllServersForRegion(List<Region> regions) {
        Objects.requireNonNull(regions, "Regions cannot be null");
        return serverRepository.findAllByRegionIn(regions).stream()
                .map(serverMapper::toServerResponse)
                .toList();
    }

    @Override
    public List<ServerResponse> getAllServers() {
        return serverRepository.findAll().stream()
                .map(serverMapper::toServerResponse)
                .toList();
    }

    public List<ServerResponse> searchServerByName(String serverName) {
        if (!StringUtils.hasText(serverName)) {
            return getAllServers();
        } else {
            return serverRepository.searchByName(serverName).stream()
                    .map(serverMapper::toServerResponse)
                    .toList();
        }
    }

    @Override
    public ServerResponse getServerResponse(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Id must be valid");
        }
       return serverRepository.findById(id)
               .map(serverMapper::toServerResponse)
                .orElseThrow(() -> new NotFoundException("No server found for id: " + id));

    }

    @Override
    public ServerResponse getServerResponse(String uniqueServerName) {
        if (!StringUtils.hasText(uniqueServerName)) {
            throw new IllegalArgumentException("Server name cannot be empty");
        }

        return serverRepository.findByUniqueName(uniqueServerName)
                .map(serverMapper::toServerResponse)
                .orElseThrow(() -> new NotFoundException("No server found with name: " + uniqueServerName));



    }
    public List<ServerResponse> getAllByFaction(Faction faction) {
        return serverRepository.findAllByFaction(faction).stream()
                .map(serverMapper::toServerResponse)
                .toList();
    }

    @Override
    @Transactional
    public void updatePopulationForServer(String serverName, PopulationResponse populationResponse) {
        if (!StringUtils.hasText(serverName)) {
            throw new IllegalArgumentException("Server name cannot be empty");
        }
        List<Server> servers = serverRepository.findByName(serverName);
        if (servers.isEmpty()) {
            throw new NotFoundException("No server found with name: " + serverName);
        }
        servers.forEach(server ->
        {
            Population serverPopulation = server.getPopulation();
            if (serverPopulation == null) {
                serverPopulation = new Population();
                serverPopulation.setServers(new ArrayList<>());
                server.setPopulation(serverPopulation);
            }
            serverPopulation.setPopAlliance(populationResponse.popAlliance());
            serverPopulation.setPopHorde(populationResponse.popHorde());

            serverRepository.save(server);
        });
    }


    @Override
    public Server getServer(String uniqueServerName) {
        if (!StringUtils.hasText(uniqueServerName)) {
            throw new IllegalArgumentException("Server name cannot be empty");
        }
        return serverRepository.findByUniqueName(uniqueServerName)
                .orElseThrow(() -> new NotFoundException("No server found with name: " + uniqueServerName));
    }

    public List<ServerResponse> getAllByName(String serverName) {
        return serverRepository.findByName(serverName).stream()
                .map(serverMapper::toServerResponse)
                .toList();
    }
}