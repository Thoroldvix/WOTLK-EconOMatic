package com.thoroldvix.economatic.population;

import com.thoroldvix.economatic.server.Faction;
import com.thoroldvix.economatic.server.Server;
import com.thoroldvix.economatic.server.ServerResponse;
import com.thoroldvix.economatic.server.ServerService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.thoroldvix.economatic.common.util.Utils.elapsedTimeInMillis;

@RequiredArgsConstructor
@Service
@Slf4j
class PopulationUpdateService {

    public static final String UPDATE_ON_STARTUP_OR_DEFAULT = "#{${economatic.update-on-startup} ? -1 : ${economatic.population.update-rate}}";
    public static final String UPDATE_RATE = "${economatic.population.update-rate}";

    @PersistenceContext
    private final EntityManager entityManager;
    private final ServerService serverServiceImpl;
    private final PopulationService populationServiceImpl;
    private final WarcraftTavernService warcraftTavernService;

    @Scheduled(fixedRateString = UPDATE_RATE,
            initialDelayString = UPDATE_ON_STARTUP_OR_DEFAULT,
            timeUnit = TimeUnit.DAYS)
    @Retryable(maxAttempts = 5)
    protected void update() {
        log.info("Updating population");
        Instant start = Instant.now();

        List<Population> populations = retrievePopulations();
        populationServiceImpl.saveAll(populations);

        log.info("Finished updating population in {} ms", elapsedTimeInMillis(start));
    }

    private List<Population> retrievePopulations() {
        List<TotalPopResponse> totalPopulationsForServer = warcraftTavernService.retrievePopulations();
        List<ServerResponse> servers = serverServiceImpl.getAll().servers();
        return toPopulationList(totalPopulationsForServer, servers);
    }

    private List<Population> toPopulationList(List<TotalPopResponse> totalPopulationsForServer, List<ServerResponse> servers) {
        return totalPopulationsForServer.stream()
                .flatMap(totalPop -> getForBothFactions(totalPop, servers).stream())
                .toList();
    }

    private List<Population> getForBothFactions(TotalPopResponse totalPop,
                                                List<ServerResponse> servers) {

        return servers.stream()
                .filter(server -> server.name().equals(totalPop.serverName()))
                .map(server -> buildPopulation(totalPop, server))
                .toList();
    }

    private Population buildPopulation(TotalPopResponse totalPopulation, ServerResponse server) {
        int populationSize = server.faction().equals(Faction.HORDE)
                ? totalPopulation.popHorde()
                : totalPopulation.popAlliance();

        Server serverEntity = entityManager.getReference(Server.class, server.id());

        return Population.builder()
                .value(populationSize)
                .server(serverEntity)
                .build();
    }
}
