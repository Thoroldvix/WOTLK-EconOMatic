package com.thoroldvix.economatic.population;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarcraftTavernService {

    private final WarcraftTavernClient warcraftTavernClient;
    private final PopulationDeserializer populationDeserializer;

    public List<TotalPopResponse> retrievePopulations() {
        String populationsJson = warcraftTavernClient.getAll();
        return populationDeserializer.extractFromJson(populationsJson);
    }
}
