package com.thoroldvix.economatic.goldprice.server.repository;

import com.thoroldvix.economatic.PostgreSqlContainerInitializer;
import com.thoroldvix.economatic.goldprice.GoldPriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class ServerPriceRepositoryTest implements PostgreSqlContainerInitializer {

    @Autowired
    private GoldPriceRepository GoldPriceRepository;

    @Autowired
    private TestEntityManager entityManager;


}