package com.thoroldvix.pricepal.population;

import com.thoroldvix.pricepal.server.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class PopulationUpdateService {

    private final WarcraftTavernClient warcraftTavernClient;
    private final ServerRepository serverRepository;
    private final ServerService serverServiceImpl;
    private final PopulationService populationService;

    @Scheduled(fixedRate = 7, timeUnit = TimeUnit.DAYS)
    protected void update() {
        updateForRegion(Region.EU);
        updateForRegion(Region.US);
    }

    private void updateForRegion(Region region) {
        Objects.requireNonNull(region, "Region cannot be null");
        log.info("Updating {} population", region.name());
        Instant start = Instant.now();

        List<ServerResponse> servers = serverServiceImpl.getAllForRegion(region);
        Set<String> serverNames = getServerNames(servers);
        List<Population> populations = retrievePopulations(region, servers, serverNames);

        populationService.saveAll(populations);
        log.info("Updated {} population in {} ms", region.name(), Duration.between(start, Instant.now()).toMillis());
    }

    private List<Population> retrievePopulations(Region region, List<ServerResponse> servers, Set<String> serverNames) {
        return serverNames.parallelStream()
                .flatMap(serverName -> {
                    String formattedServerName = formatServerName(serverName);
                    Map<String, String> warcraftTavernResponse = warcraftTavernClient.getPopulationForServer(region, formattedServerName);
                    return getForBothFactions(serverName, servers, warcraftTavernResponse).stream();
                })
                .collect(Collectors.toList());
    }

    private List<Population> getForBothFactions(String serverName,
                                                List<ServerResponse> servers,
                                                Map<String, String> warcraftTavernResponse) {

        return servers.stream()
                .filter(server -> server.name().equals(serverName))
                .map(server -> getPopulation(server, warcraftTavernResponse))
                .collect(Collectors.toList());
    }

    private Population getPopulation(ServerResponse server, Map<String, String> population) {
        int populationSize = Integer.parseInt(server.faction().equals(Faction.HORDE)
                ? population.getOrDefault("popHorde", "0")
                : population.getOrDefault("popAlliance", "0"));
        Server serverEntity = serverRepository.getReferenceById(server.id());

        return Population.builder()
                .population(populationSize)
                .server(serverEntity)
                .build();
    }

    private Set<String> getServerNames(List<ServerResponse> servers) {
        return servers.stream()
                .map(ServerResponse::name)
                .collect(Collectors.toSet());
    }

    private String formatServerName(String serverName) {
        Objects.requireNonNull(serverName, "Server name cannot be null");
        return serverName.replaceAll(" ", "-")
                .replaceAll("'", "").toLowerCase();
    }
}
