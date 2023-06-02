package com.thoroldvix.pricepal.server.service;

import com.thoroldvix.pricepal.common.SearchSpecification;
import com.thoroldvix.pricepal.common.ValidationUtils;
import com.thoroldvix.pricepal.server.dto.PopulationResponse;
import com.thoroldvix.pricepal.server.entity.Population;
import com.thoroldvix.pricepal.server.entity.Server;
import com.thoroldvix.pricepal.server.repository.PopulationRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@EnableScheduling
@Transactional(readOnly = true)
@Slf4j
public class PopulationService {
    @PersistenceContext
    private final EntityManager entityManager;
    private final PopulationRepository populationRepository;
    private final PopulationMapper populationMapper;
    private final SearchSpecification<Population> searchSpecification;


    public List<PopulationResponse> getPopulationForServer(int serverId) {
        ValidationUtils.validatePositiveInt(serverId, "Server id must be positive");
        List<Population> population = populationRepository.findByServerId(serverId);
        return populationMapper.toPopulationList(population);
    }
     public List<PopulationResponse> getPopulationForServer(String uniqueServerName) {
         ValidationUtils.validateNonEmptyString(uniqueServerName, "Server name cannot be empty");
         List<Population> population = populationRepository.findByUniqueServerName(uniqueServerName);
         return populationMapper.toPopulationList(population);
     }



    @Transactional
    public void savePopulation(int serverId, Population population) {
        Objects.requireNonNull(population, "Population cannot be null");
        ValidationUtils.validatePositiveInt(serverId, "Server id must be positive");
        Server server = entityManager.getReference(Server.class, serverId);
        population.setServer(server);
        populationRepository.save(population);
    }

    @Transactional
    public void saveAllPopulations(List<Population> populations) {
        ValidationUtils.validateListNotEmpty(populations,
                () -> new IllegalArgumentException("Population list cannot be empty"));
        populationRepository.saveAll(populations);
    }
}
