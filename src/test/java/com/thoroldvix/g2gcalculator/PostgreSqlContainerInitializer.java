package com.thoroldvix.g2gcalculator;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@Testcontainers
public interface PostgreSqlContainerInitializer {

    @Container
    PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres").withEnv("POSTGRES_SCHEMA", "wow");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dymDynamicPropertyRegistry) {

        dymDynamicPropertyRegistry.add("spring.datasource.url", () -> postgreSQLContainer.getJdbcUrl() + "?currentSchema=wow");
        dymDynamicPropertyRegistry.add("spring.datasource.driver-class-name", postgreSQLContainer::getDriverClassName);
        dymDynamicPropertyRegistry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        dymDynamicPropertyRegistry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }
}