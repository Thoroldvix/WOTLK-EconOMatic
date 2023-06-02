package com.thoroldvix.pricepal.server.service;

import com.thoroldvix.pricepal.server.api.WarcraftTavernClient;
import com.thoroldvix.pricepal.server.dto.ServerResponse;
import com.thoroldvix.pricepal.server.entity.Faction;
import com.thoroldvix.pricepal.server.entity.Region;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
@EnableScheduling
public class WarcraftTavernService {
    private final WarcraftTavernClient warcraftTavernClient;

    private final ServerService serverService;

    private final PopulationService populationService;

    @Scheduled(fixedRate = 7, timeUnit = TimeUnit.DAYS)
    protected void updatePopulation() {
        updatePopulationForRegion(Region.EU);
        updatePopulationForRegion(Region.US);
    }

    private void updatePopulationForRegion(Region region) {
        Objects.requireNonNull(region, "Region cannot be null");
        log.info(String.format("Updating %s population", region.name()));
        List<ServerResponse> servers = serverService.getAllServersForRegion(region);
        Set<String> serverNames = getServerNames(servers);

        serverNames.forEach(serverName -> {
            String formattedServerName = formatServerName(serverName);
            Map<String, String> population = warcraftTavernClient.getPopulationForServer(region, formattedServerName);
            updatePopulationForServer(serverName, servers, population);
        });
        log.info(String.format("Updated %s population", region.name()));
    }

    private void updatePopulationForServer(String serverName, List<ServerResponse> servers, Map<String, String> population) {
        List<ServerResponse> filteredServers = filterByName(serverName, servers);
        filteredServers.forEach(server -> {
            int populationSize = Integer.parseInt(getPopulationSize(server, population));
            populationService.savePopulation(server.id(), populationSize);
        });
    }

    private  List<ServerResponse> filterByName(String serverName, List<ServerResponse> servers) {
        return servers.stream()
                .filter(server -> server.name().equals(serverName))
                .toList();
    }

    private String getPopulationSize(ServerResponse serverResponse, Map<String, String> population) {
        return serverResponse.faction().equals(Faction.HORDE)
                ? population.getOrDefault("popHorde", "0")
                : population.getOrDefault("popAlliance", "0");
    }

    private  Set<String> getServerNames(List<ServerResponse> servers) {
        return servers.stream()
                .map(ServerResponse::name)
                .collect(Collectors.toSet());
    }


    private  String formatServerName(String serverName) {
        Objects.requireNonNull(serverName, "Server name cannot be null");
        return serverName.replaceAll(" ", "-")
                .replaceAll("'", "").toLowerCase();
    }
}
