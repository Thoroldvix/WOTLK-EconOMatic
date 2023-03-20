package com.example.g2gcalculator.repository;

import com.example.g2gcalculator.PostgreSqlContainerInitializer;
import com.example.g2gcalculator.model.Price;
import com.example.g2gcalculator.model.Realm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
        Realm realm = Realm.builder().name("Test Realm").build();
        entityManager.persist(realm);
        Price price1 = new Price(null,BigDecimal.valueOf(0.5), Instant.now().minus(1, ChronoUnit.HOURS), realm);
        Price price2 = new Price(null, BigDecimal.valueOf(0.4), Instant.now(), realm);
        entityManager.persist(price1);
        entityManager.persist(price2);


        Optional<Price> mostRecentPrice = ClassicPriceRepository.findMostRecentByRealmId(realm.getId());


        assertTrue(mostRecentPrice.isPresent());
        assertEquals(price2, mostRecentPrice.get());
    }
}