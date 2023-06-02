package com.thoroldvix.pricepal.server.service;

import com.thoroldvix.pricepal.common.ValidationUtils;
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


    @Transactional
    public void savePopulation(int serverId, int populationSize) {
        if (populationSize < 0) {
            throw new IllegalArgumentException("Population size must be positive");
        }
        ValidationUtils.validatePositiveInt(serverId, "Server id must be positive");
        Server server = entityManager.getReference(Server.class, serverId);
        Population population = new Population();
        population.setPopulation(populationSize);
        population.setServer(server);
        populationRepository.save(population);
    }



}
