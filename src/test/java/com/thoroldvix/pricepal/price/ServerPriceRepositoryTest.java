package com.thoroldvix.pricepal.price;

import com.thoroldvix.pricepal.PostgreSqlContainerInitializer;
import com.thoroldvix.pricepal.server.entity.Faction;
import com.thoroldvix.pricepal.server.entity.Region;
import com.thoroldvix.pricepal.server.entity.Server;
import com.thoroldvix.pricepal.server.entity.ServerPrice;
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
    private com.thoroldvix.pricepal.server.entity.PriceRepository PriceRepository;

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
        ServerPrice oldServerPrice = new ServerPrice(null, BigDecimal.valueOf(0.5), LocalDateTime.now().minus(1, ChronoUnit.HOURS),"USD", server);
        ServerPrice expectedRecentServerPrice = new ServerPrice(null, BigDecimal.valueOf(0.4), LocalDateTime.now(), "USD", server);
        entityManager.persist(oldServerPrice);
        entityManager.persist(expectedRecentServerPrice);


        Optional<ServerPrice> actualRecentPrice = PriceRepository.findMostRecentPriceByServer(server);


        assertTrue(actualRecentPrice.isPresent());
        assertEquals(expectedRecentServerPrice, actualRecentPrice.get());
    }
}