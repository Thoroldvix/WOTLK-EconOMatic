package com.thoroldvix.pricepal.server.service;

import com.thoroldvix.pricepal.common.ValidationUtils;
import com.thoroldvix.pricepal.server.dto.ServerResponse;
import com.thoroldvix.pricepal.server.entity.Faction;
import com.thoroldvix.pricepal.server.entity.Region;
import com.thoroldvix.pricepal.server.entity.Server;
import com.thoroldvix.pricepal.server.error.ServerNotFoundException;
import com.thoroldvix.pricepal.server.repository.ServerRepository;
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
public class ServerService{

    private final ServerRepository serverRepository;
    private final ServerMapper serverMapper;


    public Server getServer(int id) {
        ValidationUtils.validatePositiveInt(id,  "Server id must be a positive integer");
        return serverRepository.findById(id)
                .orElseThrow(() -> new ServerNotFoundException("No server found for ID: " + id));
    }


    public Server getServer(String uniqueServerName) {
        ValidationUtils.validateNonEmptyString(uniqueServerName, "Server name cannot be null or empty");
        return serverRepository.findByUniqueName(uniqueServerName)
                .orElseThrow(() -> new ServerNotFoundException("No server found with name: " + uniqueServerName));
    }


    public ServerResponse getServerResponse(int id) {
        ValidationUtils.validatePositiveInt(id,   "Server id must be a positive integer");
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


    public List<ServerResponse> getAllServers() {
        List<Server> servers = serverRepository.findAll();
        ValidationUtils.validateListNotEmpty(servers, () -> new ServerNotFoundException("No servers found"));
        return serverMapper.toServerResponseList(servers);
    }



    public List<ServerResponse> getAllServersForRegion(Region region) {
        Objects.requireNonNull(region, "Region cannot be null");
        List<Server> servers = serverRepository.findAllByRegion(region);
        ValidationUtils.validateListNotEmpty(servers,
                () -> new ServerNotFoundException("No servers found for region: " + region));
        return serverMapper.toServerResponseList(servers);
    }


    public List<ServerResponse> searchServerByName(String serverName) {
        return StringUtils.hasText(serverName)
                ? serverRepository.searchByName(serverName).stream()
                .map(serverMapper::toServerResponse)
                .collect(Collectors.toList())
                : getAllServers();
    }



    public List<ServerResponse> getAllServersForFaction(Faction faction) {
        Objects.requireNonNull(faction, "Faction cannot be null");
        List<Server> servers = serverRepository.findAllByFaction(faction);
        ValidationUtils.validateListNotEmpty(servers,
                () -> new ServerNotFoundException("No servers found for faction: " + faction));
        return serverMapper.toServerResponseList(servers);
    }


    public List<ServerResponse> getAllServersForName(String serverName) {
        ValidationUtils.validateNonEmptyString(serverName, "Server name cannot be null or empty");
        List<Server> servers = serverRepository.findAllByName(serverName);
        ValidationUtils.validateListNotEmpty(servers,
                () -> new ServerNotFoundException("No server found with name: " + serverName));
        return serverMapper.toServerResponseList(servers);
    }
}
