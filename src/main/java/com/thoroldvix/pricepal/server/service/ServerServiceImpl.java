package com.thoroldvix.pricepal.server.service;

import com.thoroldvix.pricepal.server.dto.FullPopulationResponse;
import com.thoroldvix.pricepal.server.dto.ServerResponse;
import com.thoroldvix.pricepal.server.entity.*;
import com.thoroldvix.pricepal.server.error.ServerNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServerServiceImpl implements ServerService {

    private final ServerRepository serverRepository;
    private final ServerMapper serverMapper;

    @Override
    public Server getServer(int id) {
        verifyServerId(id);
        return serverRepository.findById(id)
                .orElseThrow(() -> new ServerNotFoundException("No server found for ID: " + id));
    }

    @Override
    public Server getServer(String uniqueServerName) {
        verifyServerName(uniqueServerName);
        return serverRepository.findByUniqueName(uniqueServerName)
                .orElseThrow(() -> new ServerNotFoundException("No server found with name: " + uniqueServerName));
    }

    @Override
    public ServerResponse getServerResponse(int id) {
        verifyServerId(id);
        return serverRepository.findById(id)
                .map(serverMapper::toServerResponse)
                .orElseThrow(() -> new ServerNotFoundException("No server found for ID: " + id));

    }

    @Override
    public ServerResponse getServerResponse(String uniqueServerName) {
        verifyServerName(uniqueServerName);
        return serverRepository.findByUniqueName(uniqueServerName)
                .map(serverMapper::toServerResponse)
                .orElseThrow(() -> new ServerNotFoundException("No server found with name: " + uniqueServerName));
    }

    @Override
    public List<ServerResponse> getAllServers() {
        List<ServerResponse> servers = serverRepository.findAll().stream()
                .map(serverMapper::toServerResponse)
                .toList();
        if (servers.isEmpty()) {
            throw new ServerNotFoundException("No servers found");
        }
        return servers;
    }


    @Override
    public List<ServerResponse> getAllServersForRegion(Region region) {
        Objects.requireNonNull(region, "Region cannot be null");
        List<ServerResponse> servers = serverRepository.findAllByRegion(region).stream()
                .map(serverMapper::toServerResponse)
                .toList();
        if (servers.isEmpty()) {
            throw new ServerNotFoundException("No servers found for region: " + region);
        }
        return servers;
    }


    public List<ServerResponse> searchServerByName(String serverName) {
        return StringUtils.hasText(serverName)
                ? serverRepository.searchByName(serverName).stream()
                .map(serverMapper::toServerResponse)
                .collect(Collectors.toList())
                : getAllServers();
    }


    @Override
    public List<ServerResponse> getAllServersForFaction(Faction faction) {
        return serverRepository.findAllByFaction(faction).stream()
                .map(serverMapper::toServerResponse)
                .toList();
    }

    @Override
    public List<ServerResponse> getAllServersForName(String serverName) {
        List<ServerResponse> servers = serverRepository.findByName(serverName).stream()
                .map(serverMapper::toServerResponse)
                .toList();
        if (servers.isEmpty()) {
            throw new ServerNotFoundException("No server found with name: " + serverName);
        }
        return servers;
    }

    @Override
    @Transactional
    public void updatePopulationForServer(String serverName, FullPopulationResponse populationResponse) {
        verifyServerName(serverName);
        List<Server> servers = serverRepository.findByName(serverName);
        if (servers.isEmpty()) {
            throw new ServerNotFoundException("No servers found with name: " + serverName);
        }
        servers.forEach(server -> {
            if (server.getFaction().equals(Faction.HORDE)) {
                Population population = Population.builder()
                        .population(populationResponse.popHorde())
                        .build();
                server.addPopulation(population);
            }
            Population population = Population.builder()
                    .population(populationResponse.popAlliance())
                    .build();
            server.addPopulation(population);
        });
    }


    private void verifyServerName(String serverName) {
        if (!StringUtils.hasText(serverName)) {
            throw new IllegalArgumentException("Server name cannot be empty");
        }
    }

    private void verifyServerId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Server ID must be positive");
        }
    }
}
