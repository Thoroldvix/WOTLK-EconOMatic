package com.example.g2gcalculator.repository;

import com.example.g2gcalculator.PostgreSqlContainerInitializer;
import com.example.g2gcalculator.model.*;
import com.example.g2gcalculator.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static com.example.g2gcalculator.util.TestUtil.*;
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
        Realm realm =  createRealm();
        entityManager.persist(realm);
        Price oldPrice = new Price(null,BigDecimal.valueOf(0.5), LocalDateTime.now().minus(1, ChronoUnit.HOURS), realm);
        Price expectedRecentPrice = new Price(null, BigDecimal.valueOf(0.4), LocalDateTime.now(), realm);
        entityManager.persist(oldPrice);
        entityManager.persist(expectedRecentPrice);


        Optional<Price> actualRecentPrice = ClassicPriceRepository.findMostRecentPriceByRealm(realm);


        assertTrue(actualRecentPrice.isPresent());
        assertEquals(expectedRecentPrice, actualRecentPrice.get());
    }
}