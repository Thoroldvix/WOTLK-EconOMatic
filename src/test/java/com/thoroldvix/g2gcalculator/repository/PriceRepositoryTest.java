package com.thoroldvix.g2gcalculator.repository;

import com.thoroldvix.g2gcalculator.PostgreSqlContainerInitializer;
import com.thoroldvix.g2gcalculator.server.Faction;
import com.thoroldvix.g2gcalculator.price.Price;
import com.thoroldvix.g2gcalculator.server.Server;
import com.thoroldvix.g2gcalculator.price.PriceRepository;
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
class PriceRepositoryTest implements PostgreSqlContainerInitializer {

    @Autowired
    private PriceRepository PriceRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findMostRecentByRealmId_shouldWork() {
        Server server = Server.builder()
                .id(1)
                .name("test")
                .faction(Faction.HORDE)
                .prices(new ArrayList<>())
                .build();
        AuctionHouse auctionHouse = AuctionHouse.builder()
                .id(1)
                .build();
        server.setAuctionHouse(auctionHouse);

        entityManager.persist(server);
        Price oldPrice = new Price(null, BigDecimal.valueOf(0.5), LocalDateTime.now().minus(1, ChronoUnit.HOURS), server);
        Price expectedRecentPrice = new Price(null, BigDecimal.valueOf(0.4), LocalDateTime.now(), server);
        entityManager.persist(oldPrice);
        entityManager.persist(expectedRecentPrice);


        Optional<Price> actualRecentPrice = PriceRepository.findMostRecentPriceByServer(server);


        assertTrue(actualRecentPrice.isPresent());
        assertEquals(expectedRecentPrice, actualRecentPrice.get());
    }
}