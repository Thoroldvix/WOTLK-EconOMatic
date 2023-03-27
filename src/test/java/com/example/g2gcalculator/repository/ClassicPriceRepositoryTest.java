package com.example.g2gcalculator.repository;

import com.example.g2gcalculator.PostgreSqlContainerInitializer;
import com.example.g2gcalculator.model.AuctionHouse;
import com.example.g2gcalculator.model.Faction;
import com.example.g2gcalculator.model.Price;
import com.example.g2gcalculator.model.Realm;
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
class ClassicPriceRepositoryTest implements PostgreSqlContainerInitializer {

    @Autowired
    private ClassicPriceRepository ClassicPriceRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findMostRecentByRealmId_shouldWork() {
        Realm realm = Realm.builder()
                .id(1)
                .name("test")
                .faction(Faction.HORDE)
                .prices(new ArrayList<>())
                .build();
        AuctionHouse auctionHouse = AuctionHouse.builder()
                .id(1)
                .build();
        realm.setAuctionHouse(auctionHouse);

        entityManager.persist(realm);
        Price oldPrice = new Price(null, BigDecimal.valueOf(0.5), LocalDateTime.now().minus(1, ChronoUnit.HOURS), realm);
        Price expectedRecentPrice = new Price(null, BigDecimal.valueOf(0.4), LocalDateTime.now(), realm);
        entityManager.persist(oldPrice);
        entityManager.persist(expectedRecentPrice);


        Optional<Price> actualRecentPrice = ClassicPriceRepository.findMostRecentPriceByRealm(realm);


        assertTrue(actualRecentPrice.isPresent());
        assertEquals(expectedRecentPrice, actualRecentPrice.get());
    }
}