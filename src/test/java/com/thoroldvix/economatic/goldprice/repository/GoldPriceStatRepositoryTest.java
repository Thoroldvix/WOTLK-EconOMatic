package com.thoroldvix.economatic.goldprice.repository;

import com.thoroldvix.economatic.PostgresqlContainerInitializer;
import com.thoroldvix.economatic.server.model.Faction;
import com.thoroldvix.economatic.server.model.Region;
import com.thoroldvix.economatic.shared.StatsProjection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@ActiveProfiles("test")
@Sql("/sql/goldprice/gold-price-repository-data.sql")
class GoldPriceStatRepositoryTest implements PostgresqlContainerInitializer {

    public static final LocalDateTime START = LocalDateTime.parse("2023-07-04T16:07:53.195131").minusYears(2);
    public static final int SCALE = 6;

    @Autowired
    private GoldPriceStatRepository statRepository;

    private void assertStatsProjection(StatsProjection actual, BigDecimal mean, BigDecimal median, long minId, long maxId, long count) {
        assertSoftly(softly -> {
            softly.assertThat(actual.getMean()).isEqualTo(mean);
            softly.assertThat(actual.getMedian()).isEqualTo(median);
            softly.assertThat(actual.getMinId()).isEqualTo(minId);
            softly.assertThat(actual.getMaxId()).isEqualTo(maxId);
            softly.assertThat(actual.getCount()).isEqualTo(count);
            softly.assertAll();
        });
    }

    @Test
    void findForRegion_returnsCorrectStatsProjection_whenEuRegion() {
        LocalDateTime end = LocalDateTime.now();
        BigDecimal mean = BigDecimal.valueOf(0.211630).setScale(SCALE, RoundingMode.HALF_UP);
        long minId = 2L;
        long maxId = 11L;
        BigDecimal median = BigDecimal.valueOf(0.001205);
        long count = 9L;

        StatsProjection actual = statRepository.findForRegion(Region.EU.ordinal(), START, end);
        assertStatsProjection(actual, mean, median, minId, maxId, count);
    }

    @Test
    void findForRegion_returnsCorrectStatsProjection_whenUsRegion() {
        LocalDateTime end = LocalDateTime.now();
        BigDecimal mean = BigDecimal.valueOf(0.001182).setScale(SCALE, RoundingMode.HALF_UP);
        long minId = 6L;
        long maxId = 7L;
        BigDecimal median = BigDecimal.valueOf(0.001182);
        long count = 2L;

        StatsProjection actual = statRepository.findForRegion(Region.US.ordinal(), START, end);
        assertStatsProjection(actual, mean, median, minId, maxId, count);
    }

    @Test
    void findForFaction_returnsCorrectStatsProjection_whenAllianceFaction() {
        LocalDateTime end = LocalDateTime.now();
        BigDecimal mean = BigDecimal.valueOf(0.150518).setScale(SCALE, RoundingMode.HALF_UP);
        long minId = 5L;
        long maxId = 9L;
        BigDecimal median = BigDecimal.valueOf(0.100608);
        long count = 4L;

        StatsProjection actual = statRepository.findForFaction(Faction.ALLIANCE.ordinal(), START, end);
        assertStatsProjection(actual, mean, median, minId, maxId, count);
    }

    @Test
    void findForFaction_returnsCorrectStatsProjection_whenHordeFaction() {
        LocalDateTime end = LocalDateTime.now();
        BigDecimal mean = BigDecimal.valueOf(0.186423).setScale(SCALE, RoundingMode.HALF_UP);
        long minId = 2L;
        long maxId = 11L;
        BigDecimal median = BigDecimal.valueOf(0.001148);
        long count = 7L;

        StatsProjection actual = statRepository.findForFaction(Faction.HORDE.ordinal(), START, end);
        assertStatsProjection(actual, mean, median, minId, maxId, count);
    }

    @Test
    void findStatsForAll_returnsCorrectStatsProjection() {
        LocalDateTime end = LocalDateTime.now();
        BigDecimal mean = BigDecimal.valueOf(0.173367);
        BigDecimal median = BigDecimal.valueOf(0.001205);
        long minId = 2L;
        long maxId = 11L;
        long count = 11L;

        StatsProjection actual = statRepository.findStatsForAll(START, end);
        assertStatsProjection(actual, mean, median, minId, maxId, count);
    }

    @Test
    void findStatsForServer_returnsCorrectStatsProjection() {
        LocalDateTime end = LocalDateTime.now();
        BigDecimal mean = BigDecimal.valueOf(0.200285);
        BigDecimal median = BigDecimal.valueOf(0.2).setScale(SCALE, RoundingMode.HALF_UP);
        long minId = 5L;
        long maxId = 9L;
        long count = 3L;

        StatsProjection actual = statRepository.findStatsForServer(41037, START, end);
        assertStatsProjection(actual, mean, median, minId, maxId, count);
    }
}