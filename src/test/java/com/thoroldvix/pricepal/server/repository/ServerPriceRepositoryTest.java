package com.thoroldvix.pricepal.server.repository;

import com.thoroldvix.pricepal.PostgreSqlContainerInitializer;
import com.thoroldvix.pricepal.server.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class ServerPriceRepositoryTest implements PostgreSqlContainerInitializer {

    @Autowired
    private ServerPriceRepository ServerPriceRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findMostRecentByRealmId_shouldWork() {
        Server server = Server.builder()
                .id(1)
                .name("test")
                .faction(Faction.HORDE)
                .region(Region.EU)
                .serverPrices(new ArrayList<>())
                .build();

        entityManager.persist(server);

        ServerPrice oldServerPrice = ServerPrice.builder()
                .value(BigDecimal.valueOf(0.5))
                .createdAt(LocalDateTime.now().minus(1, ChronoUnit.HOURS))
                .currency(Currency.USD)
                .server(server)
                .build();

        ServerPrice expectedRecentServerPrice = ServerPrice.builder()
                .createdAt(LocalDateTime.now())
                .value(BigDecimal.valueOf(0.4))
                .currency(Currency.USD)
                .server(server)
                .build();

        entityManager.persist(oldServerPrice);
        entityManager.persist(expectedRecentServerPrice);


        Optional<ServerPrice> actualRecentPrice = ServerPriceRepository.findMostRecentPriceByServer(server);


        assertTrue(actualRecentPrice.isPresent());
        assertEquals(expectedRecentServerPrice, actualRecentPrice.get());
    }
}