package com.thoroldvix.pricepal.server.service;

import com.thoroldvix.pricepal.server.api.PopulationClient;
import com.thoroldvix.pricepal.server.dto.PopulationResponse;
import com.thoroldvix.pricepal.server.dto.ServerResponse;
import com.thoroldvix.pricepal.server.entity.Region;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class PopulationService {

    private final PopulationClient populationClient;

    private final ServerService serverServiceImpl;

    @Scheduled(fixedRate = 7, timeUnit = TimeUnit.DAYS)
    protected void updatePopulation() {
        updatePopulationForRegion(Region.EU);
        updatePopulationForRegion(Region.US);
    }


    private void updatePopulationForRegion(Region region) {
        log.info(String.format("Updating %s population", region.name()));

        List<ServerResponse> servers = serverServiceImpl.getAllServersForRegion(region == Region.EU
                ? Region.getEURegions()
                : Region.getUSRegions());

        Set<String> serverNames = servers.stream().map(ServerResponse::name).collect(Collectors.toSet());
        serverNames.forEach(serverName -> {
            String formattedServerName = formatServerName(serverName);
            PopulationResponse population = populationClient.getPopulationForServer(region, formattedServerName);
            serverServiceImpl.updatePopulationForServer(serverName, population);
        });
        log.info(String.format("Updated %s population", region.name()));
    }


    private String formatServerName(String serverName) {
        return serverName.replaceAll(" ", "-")
                .replaceAll("'", "").toLowerCase();
    }

}
