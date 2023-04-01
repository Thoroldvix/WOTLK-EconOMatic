package com.thoroldvix.g2gcalculator.server;

import com.thoroldvix.g2gcalculator.common.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServerServiceImpl implements ServerService {

    private final ServerRepository serverRepository;
    private final ServerMapper serverMapper;

    @Override
    public List<ServerResponse> getAllServers(Pageable pageable) {
        return serverRepository.findAll(pageable).getContent().stream()
                .map(serverMapper::toServerResponse)
                .toList();
    }

    @Override
    public Server getServerById(int id) {
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

    @Override
    public ServerResponse getServerResponse(String serverName) {
        String exactServerName = getExactServerName(serverName);
        Faction faction = getFaction(serverName);
        return serverRepository.findByNameAndFaction(exactServerName, faction).map(serverMapper::toServerResponse)
                .orElseThrow(() -> new NotFoundException("No server found for name: " + exactServerName + " and faction: " + faction));
    }

    @Override
    public Server getServer(String serverName) {
        String exactServerName = getExactServerName(serverName);
        Faction faction = getFaction(serverName);
        return serverRepository.findByNameAndFaction(exactServerName, faction)
                .orElseThrow(() -> new NotFoundException("No server found for name: " + exactServerName + " and faction: " + faction));
    }

    private String getExactServerName(String serverName) {
        if (!StringUtils.hasText(serverName)) {
            throw new IllegalArgumentException("Server name cannot be null or empty");
        }
        return serverName.split("-")[0];
    }

    private Faction getFaction(String serverName) {
        String[] split = serverName.split("-");
        if (split.length == 1) {
            throw new IllegalArgumentException("Server name must contain a faction");
        }
        String faction = split[split.length - 1];
        if (!Faction.contains(faction)) {
            throw new NotFoundException("No faction found for name: " + faction);
        }
        return faction.equalsIgnoreCase("horde") ? Faction.HORDE : Faction.ALLIANCE;
    }

}