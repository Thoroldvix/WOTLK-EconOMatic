package com.thoroldvix.economatic.stats.population;

import com.thoroldvix.economatic.common.dto.TimeRange;
import com.thoroldvix.economatic.error.ErrorMessages;
import com.thoroldvix.economatic.error.StatisticsNotFoundException;
import com.thoroldvix.economatic.population.PopulationResponse;
import com.thoroldvix.economatic.population.PopulationService;
import com.thoroldvix.economatic.server.Faction;
import com.thoroldvix.economatic.server.Region;
import com.thoroldvix.economatic.server.ServerResponse;
import com.thoroldvix.economatic.server.ServerService;
import com.thoroldvix.economatic.stats.StatsProjection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PopulationStatServiceImplTest {

    @Mock
    private PopulationStatRepository statRepository;
    @Mock
    private ServerService serverService;
    @Mock
    private PopulationStatMapper populationStatMapper;
    @Mock
    private PopulationService populationServiceImpl;

    @InjectMocks
    private PopulationStatServiceImpl underTest;

    @Test
    void getForServer_returnsCorrectPopulationStatResponse() {
        String uniqueServerName = "server1";
        ServerResponse serverResponse = ServerResponse.builder().build();
        when(serverService.getServer(uniqueServerName)).thenReturn(serverResponse);

        TimeRange timeRange = new TimeRange(7);
        StatsProjection statProjection = getStatsProjection(10, 2, 1, 5, 3);
        when(statRepository.findStatsByServer(serverResponse.id(), timeRange.start(), timeRange.end())).thenReturn(statProjection);

        PopulationResponse min = PopulationResponse.builder().build();
        PopulationResponse max = PopulationResponse.builder().build();

        when(populationServiceImpl.getForId(1)).thenReturn(min);
        when(populationServiceImpl.getForId(2)).thenReturn(max);

        PopulationStatResponse expected = PopulationStatResponse.builder()
                .mean(10)
                .maximum(max)
                .minimum(min)
                .median(5)
                .count(3)
                .build();
        when(populationStatMapper.toResponse(statProjection, min, max)).thenReturn(expected);
        PopulationStatResponse actual = underTest.getForServer(uniqueServerName, timeRange);

        assertThat(actual).isEqualTo(expected);
    }

    private StatsProjection getStatsProjection(Integer mean, Integer maxId, Integer minId, Integer median, long count) {
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
                return minId;
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

    @ParameterizedTest
    @NullAndEmptySource
    void getForServer_throwsIllegalArgumentException_whenServerIdentifierIsInvalid(String serverIdentifier) {
        TimeRange timeRange = new TimeRange(7);
        assertThatThrownBy(() -> underTest.getForServer(serverIdentifier, timeRange))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessages.SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY.message);
    }

    @Test
    void getForServer_throwsStatisticsNotFoundException_whenRepositoryReturnsEmptyStatProjection() {
        String serverIdentifier = "server1";
        TimeRange timeRange = new TimeRange(7);
        ServerResponse serverResponse = ServerResponse.builder().build();
        when(serverService.getServer(serverIdentifier)).thenReturn(serverResponse);

        StatsProjection emptyStatProjection = getStatsProjection(null, null, null, null, 0);
        when(statRepository.findStatsByServer(serverResponse.id(), timeRange.start(), timeRange.end())).thenReturn(emptyStatProjection);

        assertThatThrownBy(() -> underTest.getForServer(serverIdentifier, timeRange))
                .isInstanceOf(StatisticsNotFoundException.class)
                .hasMessage(ErrorMessages.NO_STATISTICS_FOUND.message);
    }

    @Test
    void getForRegion_returnsCorrectPopulationStatResponse_whenEuRegion() {
        String regionName = "eu";

        TimeRange timeRange = new TimeRange(7);
        StatsProjection statProjection = getStatsProjection(10, 2, 1, 5, 3);
        when(statRepository.findStatsByRegion(Region.EU.ordinal(), timeRange.start(), timeRange.end())).thenReturn(statProjection);

        PopulationResponse min = PopulationResponse.builder().build();
        PopulationResponse max = PopulationResponse.builder().build();

        when(populationServiceImpl.getForId(1)).thenReturn(min);
        when(populationServiceImpl.getForId(2)).thenReturn(max);

        PopulationStatResponse expected = PopulationStatResponse.builder()
                .mean(10)
                .maximum(max)
                .minimum(min)
                .median(5)
                .count(3)
                .build();
        when(populationStatMapper.toResponse(statProjection, min, max)).thenReturn(expected);

        PopulationStatResponse actual = underTest.getForRegion(regionName, timeRange);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getForRegion_returnsCorrectPopulationStatResponse_whenUsRegion() {
        String regionName = "us";

        TimeRange timeRange = new TimeRange(7);
        StatsProjection statProjection = getStatsProjection(10, 2, 1, 5, 3);
        when(statRepository.findStatsByRegion(Region.US.ordinal(), timeRange.start(), timeRange.end())).thenReturn(statProjection);

        PopulationResponse min = PopulationResponse.builder().build();
        PopulationResponse max = PopulationResponse.builder().build();

        when(populationServiceImpl.getForId(1)).thenReturn(min);
        when(populationServiceImpl.getForId(2)).thenReturn(max);

        PopulationStatResponse expected = PopulationStatResponse.builder()
                .mean(10)
                .maximum(max)
                .minimum(min)
                .median(5)
                .count(3)
                .build();
        when(populationStatMapper.toResponse(statProjection, min, max)).thenReturn(expected);

        PopulationStatResponse actual = underTest.getForRegion(regionName, timeRange);

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void getForRegion_throwsIllegalArgumentException_whenRegionNameIsInvalid(String regionName) {
        TimeRange timeRange = new TimeRange(7);
        assertThatThrownBy(() -> underTest.getForRegion(regionName, timeRange))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessages.REGION_NAME_CANNOT_BE_NULL_OR_EMPTY.message);
    }

    @Test
    void getForRegion_throwsStatisticsNotFoundException_whenRepositoryReturnsEmptyStatProjection() {
        String regionName = "eu";
        TimeRange timeRange = new TimeRange(7);

        StatsProjection emptyStatProjection = getStatsProjection(null, null, null, null, 0);
        when(statRepository.findStatsByRegion(Region.EU.ordinal(), timeRange.start(), timeRange.end())).thenReturn(emptyStatProjection);

        assertThatThrownBy(() -> underTest.getForRegion(regionName, timeRange))
                .isInstanceOf(StatisticsNotFoundException.class)
                .hasMessage(ErrorMessages.NO_STATISTICS_FOUND.message);
    }

    @Test
    void getForFaction_returnsCorrectPopulationStatResponse_whenAllianceFaction() {
        String factionName = "alliance";

        TimeRange timeRange = new TimeRange(7);
        StatsProjection statProjection = getStatsProjection(10, 2, 1, 5, 3);
        when(statRepository.findStatsByFaction(Faction.ALLIANCE.ordinal(), timeRange.start(), timeRange.end())).thenReturn(statProjection);

        PopulationResponse min = PopulationResponse.builder().build();
        PopulationResponse max = PopulationResponse.builder().build();

        when(populationServiceImpl.getForId(1)).thenReturn(min);
        when(populationServiceImpl.getForId(2)).thenReturn(max);

        PopulationStatResponse expected = PopulationStatResponse.builder()
                .mean(10)
                .maximum(max)
                .minimum(min)
                .median(5)
                .count(3)
                .build();
        when(populationStatMapper.toResponse(statProjection, min, max)).thenReturn(expected);

        PopulationStatResponse actual = underTest.getForFaction(factionName, timeRange);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getForFaction_returnsCorrectPopulationStatResponse_whenHordeFaction() {
        String factionName = "horde";

        TimeRange timeRange = new TimeRange(7);
        StatsProjection statProjection = getStatsProjection(10, 2, 1, 5, 3);
        when(statRepository.findStatsByFaction(Faction.HORDE.ordinal(), timeRange.start(), timeRange.end())).thenReturn(statProjection);

        PopulationResponse min = PopulationResponse.builder().build();
        PopulationResponse max = PopulationResponse.builder().build();

        when(populationServiceImpl.getForId(1)).thenReturn(min);
        when(populationServiceImpl.getForId(2)).thenReturn(max);

        PopulationStatResponse expected = PopulationStatResponse.builder()
                .mean(10)
                .maximum(max)
                .minimum(min)
                .median(5)
                .count(3)
                .build();
        when(populationStatMapper.toResponse(statProjection, min, max)).thenReturn(expected);

        PopulationStatResponse actual = underTest.getForFaction(factionName, timeRange);

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void getForFaction_throwsIllegalArgumentException_whenFactionNameIsInvalid(String factionName) {
        TimeRange timeRange = new TimeRange(7);
        assertThatThrownBy(() -> underTest.getForFaction(factionName, timeRange))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessages.FACTION_NAME_CANNOT_BE_NULL_OR_EMPTY.message);
    }

    @Test
    void getForFaction_throwsStatisticsNotFoundException_whenRepositoryReturnsEmptyStatProjection() {
        String factionName = "alliance";
        TimeRange timeRange = new TimeRange(7);

        StatsProjection emptyStatProjection = getStatsProjection(null, null, null, null, 0);
        when(statRepository.findStatsByFaction(Faction.ALLIANCE.ordinal(), timeRange.start(), timeRange.end())).thenReturn(emptyStatProjection);

        assertThatThrownBy(() -> underTest.getForFaction(factionName, timeRange))
                .isInstanceOf(StatisticsNotFoundException.class)
                .hasMessage(ErrorMessages.NO_STATISTICS_FOUND.message);
    }

    @Test
    void getForAll_returnsCorrectPopulationStatResponse() {
        TimeRange timeRange = new TimeRange(7);
        StatsProjection statProjection = getStatsProjection(10, 2, 1, 5, 3);
        when(statRepository.findForTimeRange(timeRange.start(), timeRange.end())).thenReturn(statProjection);

        PopulationResponse min = PopulationResponse.builder().build();
        PopulationResponse max = PopulationResponse.builder().build();

        when(populationServiceImpl.getForId(1)).thenReturn(min);
        when(populationServiceImpl.getForId(2)).thenReturn(max);

        PopulationStatResponse expected = PopulationStatResponse.builder()
                .mean(10)
                .maximum(max)
                .minimum(min)
                .median(5)
                .count(3)
                .build();
        when(populationStatMapper.toResponse(statProjection, min, max)).thenReturn(expected);

        PopulationStatResponse actual = underTest.getForAll(timeRange);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getForAll_throwsStatisticsNotFoundException_whenRepositoryReturnsEmptyStatProjection() {
        TimeRange timeRange = new TimeRange(7);

        StatsProjection emptyStatProjection = getStatsProjection(null, null, null, null, 0);
        when(statRepository.findForTimeRange(timeRange.start(), timeRange.end())).thenReturn(emptyStatProjection);

        assertThatThrownBy(() -> underTest.getForAll(timeRange))
                .isInstanceOf(StatisticsNotFoundException.class)
                .hasMessage(ErrorMessages.NO_STATISTICS_FOUND.message);
    }
}