package com.thoroldvix.economatic.goldprice.repository;

import com.thoroldvix.economatic.PostgresqlContainerInitializer;
import com.thoroldvix.economatic.goldprice.GoldPriceSetupBaseTest;
import com.thoroldvix.economatic.goldprice.model.GoldPrice;
import com.thoroldvix.economatic.server.model.Faction;
import com.thoroldvix.economatic.server.model.Region;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Sql("/sql/goldprice/gold-price-repository-data.sql")
class GoldPriceRepositoryTest extends GoldPriceSetupBaseTest implements PostgresqlContainerInitializer {

    public static final int SERVER_ID = 41037;

    @Autowired
    private GoldPriceRepository goldPriceRepository;

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

