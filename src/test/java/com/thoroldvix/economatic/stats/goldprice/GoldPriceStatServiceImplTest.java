package com.thoroldvix.economatic.stats.goldprice;

import com.thoroldvix.economatic.error.StatisticsNotFoundException;
import com.thoroldvix.economatic.goldprice.GoldPriceResponse;
import com.thoroldvix.economatic.goldprice.GoldPriceService;
import com.thoroldvix.economatic.server.Faction;
import com.thoroldvix.economatic.server.Region;
import com.thoroldvix.economatic.server.ServerResponse;
import com.thoroldvix.economatic.server.ServerService;
import com.thoroldvix.economatic.shared.TimeRange;
import com.thoroldvix.economatic.stats.StatsProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;


@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class GoldPriceStatServiceImplTest {
    @Mock
    private ServerService serverService;
    @Mock
    private GoldPriceStatRepository goldPriceStatRepository;
    @Mock
    private GoldPriceStatMapper goldPriceStatMapper;
    @Mock
    private GoldPriceService goldPriceService;

    @InjectMocks
    private GoldPriceStatServiceImpl goldPriceStatServiceImpl;
    private String serverIdentifier;
    private TimeRange timeRange;

    private static StatsProjection getStatsProjection(BigDecimal mean, BigDecimal median, Number minId, Number maxId, long count) {
        return new StatsProjection() {
            @Override
            public Number getMean() {
                return mean;
            }

            @Override
            public Number getMaxId() {
                return maxId;
            }

            @Override
            public Number getMinId() {
                return maxId;
            }

            @Override
            public Number getMedian() {
                return median;
            }

            @Override
            public long getCount() {
                return count;
            }
        };
    }

    @BeforeEach
    void setUp() {
        serverIdentifier = "server1-alliance";
        timeRange = new TimeRange(7);
    }

    @Test
    void getForServer_returnsCorrectGoldPriceStatResponse() {
        ServerResponse server = ServerResponse.builder().id(1).build();
        when(serverService.getServer(serverIdentifier)).thenReturn(server);

        long count = 5;
        StatsProjection statsProjection = getStatsProjection(BigDecimal.TEN, BigDecimal.TEN, 1, 2, count);
        when(goldPriceStatRepository.findStatsForServer(server.id(), timeRange.start(), timeRange.end())).thenReturn(statsProjection);

        GoldPriceResponse min = GoldPriceResponse.builder().build();
        when(goldPriceService.getForId(statsProjection.getMinId().longValue())).thenReturn(min);

        GoldPriceResponse max = GoldPriceResponse.builder().build();
        when(goldPriceService.getForId(statsProjection.getMaxId().longValue())).thenReturn(max);

        GoldPriceStatResponse expected = getStatResponse(min, max, count);
        when(goldPriceStatMapper.toResponse(statsProjection, min, max)).thenReturn(expected);

        GoldPriceStatResponse actual = goldPriceStatServiceImpl.getForServer(serverIdentifier, timeRange);

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void getForServer_throwsIllegalArgumentException_whenServerIdentifierIsInvalid(String serverIdentifier) {
        assertThatThrownBy(() -> goldPriceStatServiceImpl.getForServer(serverIdentifier, timeRange))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getForServer_throwsNullPointerException_whenTimeRangeIsNull() {
        assertThatThrownBy(() -> goldPriceStatServiceImpl.getForServer(serverIdentifier, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void getForServer_throwsStatisticsNotFoundException_whenRepositoryReturnsInvalidStatProjection() {
        ServerResponse server = ServerResponse.builder().id(1).build();
        when(serverService.getServer(serverIdentifier)).thenReturn(server);

        StatsProjection statsProjection = getStatsProjection(null, null, null, null, 0);
        when(goldPriceStatRepository.findStatsForServer(server.id(), timeRange.start(), timeRange.end())).thenReturn(statsProjection);

        assertThatThrownBy(() -> goldPriceStatServiceImpl.getForServer(serverIdentifier, timeRange))
                .isInstanceOf(StatisticsNotFoundException.class);
    }

    @Test
    void getForRegion_returnsCorrectGoldPriceStatResponse_whenEuRegion() {
        long count = 5;
        StatsProjection statsProjection = getStatsProjection(BigDecimal.TEN, BigDecimal.TEN, 1, 2, count);
        when(goldPriceStatRepository.findForRegion(Region.EU.ordinal(), timeRange.start(), timeRange.end())).thenReturn(statsProjection);

        GoldPriceResponse min = GoldPriceResponse.builder().build();
        when(goldPriceService.getForId(statsProjection.getMinId().longValue())).thenReturn(min);

        GoldPriceResponse max = GoldPriceResponse.builder().build();
        when(goldPriceService.getForId(statsProjection.getMaxId().longValue())).thenReturn(max);

        GoldPriceStatResponse expectedResponse = getStatResponse(min, max, count);
        when(goldPriceStatMapper.toResponse(statsProjection, min, max)).thenReturn(expectedResponse);

        String regionName = "eu";
        GoldPriceStatResponse actual = goldPriceStatServiceImpl.getForRegion(regionName, timeRange);


        assertThat(actual).isEqualTo(expectedResponse);
    }


    @Test
    void getForRegion_returnsCorrectGoldPriceStatResponse_whenUsRegion() {
        long count = 5;
        StatsProjection statsProjection = getStatsProjection(BigDecimal.TEN, BigDecimal.TEN, 1, 2, count);
        when(goldPriceStatRepository.findForRegion(Region.US.ordinal(), timeRange.start(), timeRange.end())).thenReturn(statsProjection);

        GoldPriceResponse min = GoldPriceResponse.builder().build();
        when(goldPriceService.getForId(statsProjection.getMinId().longValue())).thenReturn(min);

        GoldPriceResponse max = GoldPriceResponse.builder().build();
        when(goldPriceService.getForId(statsProjection.getMaxId().longValue())).thenReturn(max);

        GoldPriceStatResponse expectedResponse = getStatResponse(min, max, count);
        when(goldPriceStatMapper.toResponse(statsProjection, min, max)).thenReturn(expectedResponse);

        String regionName = "us";
        GoldPriceStatResponse actual = goldPriceStatServiceImpl.getForRegion(regionName, timeRange);


        assertThat(actual).isEqualTo(expectedResponse);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void getForRegion_throwsIllegalArgumentException_whenRegionNameIsInvalid(String regionName) {
        assertThatThrownBy(() -> goldPriceStatServiceImpl.getForRegion(regionName, timeRange))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getForRegion_throwsNullPointerException_whenTimeRangeIsNull() {
        assertThatThrownBy(() -> goldPriceStatServiceImpl.getForRegion("eu", null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void getForFaction_returnsCorrectGoldPriceStatResponse_whenAllianceFaction() {
        long count = 5;
        StatsProjection statsProjection = getStatsProjection(BigDecimal.TEN, BigDecimal.TEN, 1, 2, count);
        when(goldPriceStatRepository.findForFaction(Faction.ALLIANCE.ordinal(), timeRange.start(), timeRange.end())).thenReturn(statsProjection);

        GoldPriceResponse min = GoldPriceResponse.builder().build();
        when(goldPriceService.getForId(statsProjection.getMinId().longValue())).thenReturn(min);

        GoldPriceResponse max = GoldPriceResponse.builder().build();
        when(goldPriceService.getForId(statsProjection.getMaxId().longValue())).thenReturn(max);

        GoldPriceStatResponse expectedResponse = getStatResponse(min, max, count);
        when(goldPriceStatMapper.toResponse(statsProjection, min, max)).thenReturn(expectedResponse);

        String regionName = "alliance";
        GoldPriceStatResponse actual = goldPriceStatServiceImpl.getForFaction(regionName, timeRange);


        assertThat(actual).isEqualTo(expectedResponse);
    }

    @Test
    void getForFaction_returnsCorrectGoldPriceStatResponse_whenHordeFaction() {
        long count = 5;
        StatsProjection statsProjection = getStatsProjection(BigDecimal.TEN, BigDecimal.TEN, 1, 2, count);
        when(goldPriceStatRepository.findForFaction(Faction.HORDE.ordinal(), timeRange.start(), timeRange.end())).thenReturn(statsProjection);

        GoldPriceResponse min = GoldPriceResponse.builder().build();
        when(goldPriceService.getForId(statsProjection.getMinId().longValue())).thenReturn(min);

        GoldPriceResponse max = GoldPriceResponse.builder().build();
        when(goldPriceService.getForId(statsProjection.getMaxId().longValue())).thenReturn(max);

        GoldPriceStatResponse expectedResponse = getStatResponse(min, max, count);
        when(goldPriceStatMapper.toResponse(statsProjection, min, max)).thenReturn(expectedResponse);

        String regionName = "horde";
        GoldPriceStatResponse actual = goldPriceStatServiceImpl.getForFaction(regionName, timeRange);


        assertThat(actual).isEqualTo(expectedResponse);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void getForFaction_throwsIllegalArgumentException_whenFactionNameIsInvalid(String factionName) {
        assertThatThrownBy(() -> goldPriceStatServiceImpl.getForFaction(factionName, timeRange))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getForFaction_throwsNullPointerException_whenTimeRangeIsNull() {
        assertThatThrownBy(() -> goldPriceStatServiceImpl.getForFaction("alliance", null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void getForAll_returnsCorrectGoldPriceStatResponse() {
        long count = 5;
        StatsProjection statsProjection = getStatsProjection(BigDecimal.TEN, BigDecimal.TEN, 1, 2, count);
        when(goldPriceStatRepository.findStatsForAll(timeRange.start(), timeRange.end())).thenReturn(statsProjection);

        GoldPriceResponse min = GoldPriceResponse.builder().build();
        when(goldPriceService.getForId(statsProjection.getMinId().longValue())).thenReturn(min);

        GoldPriceResponse max = GoldPriceResponse.builder().build();
        when(goldPriceService.getForId(statsProjection.getMaxId().longValue())).thenReturn(max);

        GoldPriceStatResponse expectedResponse = getStatResponse(min, max, count);
        when(goldPriceStatMapper.toResponse(statsProjection, min, max)).thenReturn(expectedResponse);


        GoldPriceStatResponse actual = goldPriceStatServiceImpl.getForAll(timeRange);


        assertThat(actual).isEqualTo(expectedResponse);
    }

     @Test
    void getForAll_throwsNullPointerException_whenTimeRangeIsNull() {
        assertThatThrownBy(() -> goldPriceStatServiceImpl.getForAll(null))
                .isInstanceOf(NullPointerException.class);
    }

    private GoldPriceStatResponse getStatResponse(GoldPriceResponse min, GoldPriceResponse max, long count) {
        return GoldPriceStatResponse.builder()
                .mean(BigDecimal.TEN)
                .median(BigDecimal.TEN)
                .count(count)
                .maximum(max)
                .minimum(min)
                .build();
    }
}