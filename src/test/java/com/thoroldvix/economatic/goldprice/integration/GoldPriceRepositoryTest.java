package com.thoroldvix.economatic.goldprice.integration;

import com.thoroldvix.economatic.goldprice.GoldPrice;
import com.thoroldvix.economatic.goldprice.GoldPriceRepository;
import com.thoroldvix.economatic.server.Faction;
import com.thoroldvix.economatic.server.Region;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class GoldPriceRepositoryTest extends AbstractGoldPriceRepositoryTest {

    @Autowired
    private GoldPriceRepository goldPriceRepository;


    @Test
    void findAllRecent_returnsCorrectGoldPriceList() {
        List<GoldPrice> actual = goldPriceRepository.findAllRecent();
        assertThat(actual)
                .hasSize(8)
                .containsExactlyInAnyOrder
                        (
                                goldPrice1,
                                goldPrice2,
                                goldPrice3,
                                goldPrice4,
                                goldPrice5,
                                goldPrice6,
                                goldPrice7,
                                goldPrice8
                        );
    }

    @Test
    void findAllForServerAndTimeRange_returnsCorrectGoldPricePage() {
        LocalDateTime start = UPDATE_DATE.minusYears(2);

        List<GoldPrice> actual = goldPriceRepository.findAllForServerAndTimeRange(1, start, UPDATE_DATE, Pageable.unpaged()).getContent();
        assertThat(actual)
                .hasSize(2)
                .containsExactlyInAnyOrder(goldPrice1, goldPrice9);
    }

    @Test
    void findAllForTimeRange_returnsCorrectGoldPricePage() {
        LocalDateTime start = UPDATE_DATE.minusYears(2);

        List<GoldPrice> actual = goldPriceRepository.findAllForTimeRange(start, UPDATE_DATE, Pageable.unpaged()).getContent();
        assertThat(actual)
                .hasSize(9)
                .containsExactlyInAnyOrder(
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
        assertThat(actual)
                .hasSize(4)
                .containsExactlyInAnyOrder(
                        goldPrice5,
                        goldPrice6,
                        goldPrice7,
                        goldPrice8
                );
    }

    @Test
    void findRecentForRegion_returnsCorrectGoldPriceList_whenEURegion() {
        List<GoldPrice> actual = goldPriceRepository.findRecentForRegion(Region.EU.ordinal());
        assertThat(actual)
                .hasSize(4)
                .containsExactlyInAnyOrder(
                        goldPrice1,
                        goldPrice2,
                        goldPrice3,
                        goldPrice4
                );
    }

    @Test
    void findRecentForFaction_returnsCorrectGoldPriceList_whenAllianceFaction() {
        List<GoldPrice> actual = goldPriceRepository.findRecentForFaction(Faction.ALLIANCE.ordinal());
        assertThat(actual)
                .hasSize(4)
                .containsExactlyInAnyOrder(
                        goldPrice1,
                        goldPrice2,
                        goldPrice5,
                        goldPrice6
                );
    }

    @Test
    void findRecentForFaction_returnsCorrectGoldPriceList_whenHordeFaction() {
        List<GoldPrice> actual = goldPriceRepository.findRecentForFaction(Faction.HORDE.ordinal());
        assertThat(actual)
                .hasSize(4)
                .containsExactlyInAnyOrder(
                        goldPrice3,
                        goldPrice4,
                        goldPrice7,
                        goldPrice8
                );
    }

    @Test
    void findRecentForServer_returnsCorrectGoldPriceOptional() {
        Optional<GoldPrice> actual = goldPriceRepository.findRecentForServer(1);
        assertThat(actual).isPresent().contains(goldPrice1);
    }

    @Test
    void findRecentForServers_returnsCorrectGoldPriceList() {
        Set<Integer> serverIds = Set.of(1, 2, 3);
        List<GoldPrice> actual = goldPriceRepository.findRecentForServers(serverIds);
        assertThat(actual)
                .hasSize(3)
                .containsExactlyInAnyOrder(goldPrice1, goldPrice2, goldPrice3);
    }


}

