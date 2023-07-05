package com.thoroldvix.economatic.goldprice.repository;

import com.thoroldvix.economatic.PostgresqlContainerInitializer;
import com.thoroldvix.economatic.goldprice.model.GoldPrice;
import com.thoroldvix.economatic.server.model.Faction;
import com.thoroldvix.economatic.server.model.Region;
import com.thoroldvix.economatic.server.model.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Sql("/sql/goldprice/gold-price-repository-data.sql")
class GoldPriceRepositoryTest implements PostgresqlContainerInitializer {

    public static final int SERVER_ID = 41037;
    private final LocalDateTime updateDate = LocalDateTime.parse("2023-07-04T16:07:53.195131");
    GoldPrice goldPrice1;
    GoldPrice goldPrice2;
    GoldPrice goldPrice3;
    GoldPrice goldPrice4;
    GoldPrice goldPrice5;
    GoldPrice goldPrice6;
    GoldPrice goldPrice7;
    GoldPrice goldPrice8;
    GoldPrice goldPrice9;
    GoldPrice goldPrice10;
    GoldPrice goldPrice11;

    @Autowired
    private GoldPriceRepository goldPriceRepository;

    private static Server createServer(int id, Region region, Faction faction) {
        return Server.builder()
                .id(id)
                .region(region)
                .faction(faction)
                .build();
    }

    @BeforeEach
    void setup() {
        Server server1 = createServer(41074, Region.EU, Faction.HORDE);
        Server server2 = createServer(46326, Region.EU, Faction.HORDE);
        Server server3 = createServer(41070, Region.EU, Faction.HORDE);
        Server server4 = createServer(41066, Region.EU, Faction.HORDE);
        Server server5 = createServer(41037, Region.EU, Faction.HORDE);
        Server server6 = createServer(46320, Region.US, Faction.HORDE);
        Server server7 = createServer(41310, Region.US, Faction.ALLIANCE);

        goldPrice1 = new GoldPrice(1L, BigDecimal.valueOf(0.000870), updateDate, server1);
        goldPrice2 = new GoldPrice(2L, BigDecimal.valueOf(0.000801), updateDate, server2);
        goldPrice3 = new GoldPrice(3L, BigDecimal.valueOf(0.001205), updateDate, server3);
        goldPrice4 = new GoldPrice(4L, BigDecimal.valueOf(0.000938), updateDate, server4);
        goldPrice5 = new GoldPrice(5L, BigDecimal.valueOf(0.000854), updateDate, server5);
        goldPrice6 = new GoldPrice(6L, BigDecimal.valueOf(0.001148), updateDate, server6);
        goldPrice7 = new GoldPrice(7L, BigDecimal.valueOf(0.001216), updateDate, server7);
        goldPrice8 = new GoldPrice(8L, BigDecimal.valueOf(0.3), LocalDateTime.parse("2023-01-04T16:07:53.195131"), server3);
        goldPrice9 = new GoldPrice(9L, BigDecimal.valueOf(0.4), LocalDateTime.parse("2022-07-04T16:07:53.195131"), server5);
        goldPrice10 = new GoldPrice(10L, BigDecimal.valueOf(0.2), LocalDateTime.parse("2021-07-04T16:07:53.195131"), server5);
        goldPrice11 = new GoldPrice(11L, BigDecimal.valueOf(1), LocalDateTime.parse("2021-07-04T16:07:53.195131"), server2);
    }

    @Test
    void findAllRecent_returnsCorrectGoldPriceList() {
        List<GoldPrice> actual = goldPriceRepository.findAllRecent();
        assertThat(actual).hasSize(7).containsExactlyInAnyOrder(
                goldPrice1,
                goldPrice2,
                goldPrice3,
                goldPrice4,
                goldPrice5,
                goldPrice6,
                goldPrice7
        );
    }

    @Test
    void findAllForServerAndTimeRange_returnsCorrectGoldPricePage() {
        LocalDateTime start = LocalDateTime.now().minusYears(2);
        LocalDateTime end = LocalDateTime.now();

        List<GoldPrice> actual = goldPriceRepository.findAllForServerAndTimeRange(SERVER_ID, start, end, Pageable.unpaged()).getContent();
        assertThat(actual).hasSize(2).containsExactlyInAnyOrder(goldPrice5, goldPrice9);
    }

    @Test
    void findAllForTimeRange_returnsCorrectGoldPricePage() {
        LocalDateTime start = LocalDateTime.now().minusYears(2);
        LocalDateTime end = LocalDateTime.now();

        List<GoldPrice> actual = goldPriceRepository.findAllForTimeRange(start, end, Pageable.unpaged()).getContent();
        assertThat(actual).hasSize(9).containsExactlyInAnyOrder(
                goldPrice1,
                goldPrice2,
                goldPrice3,
                goldPrice4,
                goldPrice5,
                goldPrice6,
                goldPrice7,
                goldPrice8,
                goldPrice9
        );
    }

    @Test
    void findRecentForRegion_returnsCorrectGoldPriceList_whenUSRegion() {
        List<GoldPrice> actual = goldPriceRepository.findRecentForRegion(Region.US.ordinal());
        assertThat(actual).hasSize(2).containsExactlyInAnyOrder(goldPrice6, goldPrice7);
    }

    @Test
    void findRecentForRegion_returnsCorrectGoldPriceList_whenEURegion() {
        List<GoldPrice> actual = goldPriceRepository.findRecentForRegion(Region.EU.ordinal());
        assertThat(actual).hasSize(5).containsExactlyInAnyOrder(
                goldPrice1,
                goldPrice2,
                goldPrice3,
                goldPrice4,
                goldPrice5
        );
    }

    @Test
    void findRecentForFaction_returnsCorrectGoldPriceList_whenAllianceFaction() {
        List<GoldPrice> actual = goldPriceRepository.findRecentForFaction(Faction.ALLIANCE.ordinal());
        assertThat(actual).hasSize(2).containsExactlyInAnyOrder(goldPrice5, goldPrice7);
    }

    @Test
    void findRecentForFaction_returnsCorrectGoldPriceList_whenHordeFaction() {
        List<GoldPrice> actual = goldPriceRepository.findRecentForFaction(Faction.HORDE.ordinal());
        assertThat(actual).hasSize(5).containsExactlyInAnyOrder(
                goldPrice1,
                goldPrice2,
                goldPrice3,
                goldPrice4,
                goldPrice6
        );
    }

    @Test
    void findRecentForServer_returnsCorrectGoldPriceOptional() {
        Optional<GoldPrice> actual = goldPriceRepository.findRecentForServer(SERVER_ID);
        assertThat(actual).isPresent().contains(goldPrice5);
    }

    @Test
    void findRecentForServers_returnsCorrectGoldPriceList() {
        Set<Integer> serverIds = Set.of(SERVER_ID, 41074, 46326);
        List<GoldPrice> actual = goldPriceRepository.findRecentForServers(serverIds);
        assertThat(actual).hasSize(3)
                .containsExactlyInAnyOrder(goldPrice1, goldPrice2, goldPrice5);
    }
}

