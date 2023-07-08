package com.thoroldvix.economatic.goldprice.integration.repository;

import com.thoroldvix.economatic.PostgresqlContainerInitializer;
import com.thoroldvix.economatic.goldprice.model.GoldPrice;
import com.thoroldvix.economatic.server.model.Faction;
import com.thoroldvix.economatic.server.model.Region;
import com.thoroldvix.economatic.server.model.Server;
import com.thoroldvix.economatic.server.model.ServerType;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;


@ActiveProfiles(profiles = {"test", "integration"})
abstract class AbstractGoldPriceRepositoryTest implements PostgresqlContainerInitializer {
    protected final static LocalDateTime UPDATE_DATE = LocalDateTime.now();
    protected GoldPrice goldPrice1;
    protected GoldPrice goldPrice2;
    protected GoldPrice goldPrice3;
    protected GoldPrice goldPrice4;
    protected GoldPrice goldPrice5;
    protected GoldPrice goldPrice6;
    protected GoldPrice goldPrice7;
    protected GoldPrice goldPrice8;
    protected GoldPrice goldPrice9;

    @Autowired
    protected TestEntityManager entityManager;


    @BeforeEach
    void setup() {
        Server server1 = new Server(1, "server1", Region.EU, Faction.ALLIANCE, Locale.GERMAN, ServerType.PVE, "server1-alliance");
        Server server2 = new Server(2, "server2", Region.EU, Faction.ALLIANCE, Locale.ENGLISH, ServerType.PVP, "server2-alliance");
        Server server3 = new Server(3, "server3", Region.EU, Faction.HORDE, Locale.GERMAN, ServerType.RP, "server3-horde");
        Server server4 = new Server(4, "server4", Region.EU, Faction.HORDE, Locale.ENGLISH, ServerType.PVP_RP, "server4-horde");
        Server server5 = new Server(5, "server5", Region.US, Faction.ALLIANCE, Locale.US, ServerType.PVE, "server5-alliance");
        Server server6 = new Server(6, "server6", Region.US, Faction.ALLIANCE, Locale.US, ServerType.PVP, "server6-alliance");
        Server server7 = new Server(7, "server7", Region.US, Faction.HORDE, Locale.US, ServerType.RP, "server7-horde");
        Server server8 = new Server(8, "server8", Region.US, Faction.HORDE, Locale.US, ServerType.PVP_RP, "server8-horde");

        persistAll(List.of(
                server1,
                server2,
                server3,
                server4,
                server5,
                server6,
                server7,
                server8
        ));

        goldPrice1 = buildGoldPrice(0.1, UPDATE_DATE, server1);
        goldPrice2 = buildGoldPrice(0.2, UPDATE_DATE, server2);
        goldPrice3 = buildGoldPrice(0.3, UPDATE_DATE, server3);
        goldPrice4 = buildGoldPrice(0.4, UPDATE_DATE, server4);
        goldPrice5 = buildGoldPrice(0.5, UPDATE_DATE, server5);
        goldPrice6 = buildGoldPrice(0.6, UPDATE_DATE, server6);
        goldPrice7 = buildGoldPrice(0.7, UPDATE_DATE, server7);
        goldPrice8 = buildGoldPrice(0.8, UPDATE_DATE, server8);
        goldPrice9 = buildGoldPrice(0.9, UPDATE_DATE.minusYears(2), server1);
        GoldPrice goldPrice10 = buildGoldPrice(1, UPDATE_DATE.minusYears(3), server1);

        persistAll(List.of(
                goldPrice1,
                goldPrice2,
                goldPrice3,
                goldPrice4,
                goldPrice5,
                goldPrice6,
                goldPrice7,
                goldPrice8,
                goldPrice9,
                goldPrice10));
    }



    private GoldPrice buildGoldPrice(double value, LocalDateTime updateDate, Server server) {
        return GoldPrice.builder()
                .value(BigDecimal.valueOf(value))
                .updatedAt(updateDate)
                .server(server)
                .build();
    }

    private <E> void persistAll(List<E> entities) {
        entities.forEach(entityManager::persistAndFlush);
    }
}
