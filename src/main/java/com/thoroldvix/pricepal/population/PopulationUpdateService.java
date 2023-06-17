package com.thoroldvix.pricepal.population;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.thoroldvix.pricepal.server.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Service
@Slf4j
public final class PopulationUpdateService {

    private final WarcraftTavernClient warcraftTavernClient;
    private final ServerRepository serverRepository;
    private final ServerService serverServiceImpl;
    private final PopulationService populationService;

        @Scheduled(fixedRate = 1, timeUnit = TimeUnit.DAYS)
    private void update() {
        log.info("Updating population");
        Instant start = Instant.now();
        String populationJson = warcraftTavernClient.getAll();
        List<Population> populations = retrievePopulations(populationJson);
        populationService.saveAll(populations);
        log.info("Finished updating population in {} ms", Duration.between(start, Instant.now()).toMillis());
    }


    private List<Population> retrievePopulations(String populationJson) {
        List<ServerResponse> servers = serverServiceImpl.getAll();
        List<TotalPopResponse> totalPopulationsForServer = extractFromJson(populationJson);
        return getPopulationList(totalPopulationsForServer, servers);
    }

    private List<Population> getPopulationList(List<TotalPopResponse> totalPopulationsForServer, List<ServerResponse> servers) {
        return totalPopulationsForServer.stream()
                .flatMap(totalPop -> getForBothFactions(totalPop, servers).stream())
                .collect(Collectors.toList());
    }

    private List<Population> getForBothFactions(TotalPopResponse totalPop,
                                                List<ServerResponse> servers) {

        return servers.stream()
                .filter(server -> server.name().equals(totalPop.name()))
                .map(server -> getPopulation(totalPop, server))
                .collect(Collectors.toList());
    }

    private Population getPopulation(TotalPopResponse totalPopulation, ServerResponse server) {
        int populationSize = server.faction().equals(Faction.HORDE)
                ? totalPopulation.popHorde()
                : totalPopulation.popAlliance();

        Server serverEntity = serverRepository.getReferenceById(server.id());

        return Population.builder()
                .population(populationSize)
                .server(serverEntity)
                .build();
    }

    private List<TotalPopResponse> extractFromJson(String populationJson) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, TotalPopResponse.class);
        try {
            return mapper.readValue(populationJson, collectionType);
        } catch (JsonProcessingException e) {
            throw new PopulationParsingException("Error while parsing population json", e);
        }
    }

}
