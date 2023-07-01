package com.thoroldvix.economatic.population.repository;

public interface TotalPopProjection {
    Integer getPopTotal();
    Integer getPopHorde();
    Integer getPopAlliance();
    String getServerName();
}
