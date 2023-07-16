package com.thoroldvix.economatic.summary.server;

import com.thoroldvix.economatic.summary.server.ServerSummaryResponse.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServerSummaryServiceImplTest {

    @Mock
    private ServerSummaryRepository serverRepository;
    @Mock
    private ServerSummaryMapper serverSummaryMapper;

    @InjectMocks
    private ServerSummaryServiceImpl underTest;

    @Test
    void getSummary_returnsCorrectServerSummaryResponse() {
        ServerSummaryProjection summaryProjection = getSummaryProjection();
        Faction faction = buildFaction();
        Region region = buildRegion();
        Type type = buildType();
        Locale locale = buildLocale();
        int total = 96;
        Summary summary = new Summary(faction, region, type, locale, total);
        ServerSummaryResponse expected = new ServerSummaryResponse(summary);

        when(serverRepository.getSummary()).thenReturn(summaryProjection);
        when(serverSummaryMapper.toResponse(summaryProjection)).thenReturn(expected);

        ServerSummaryResponse actual = underTest.getSummary();

        assertThat(actual).isEqualTo(expected);
    }

    private static Locale buildLocale() {
        return Locale.builder()
                .enGB(9)
                .enUS(10)
                .deDE(11)
                .esES(12)
                .frFR(13)
                .ruRU(14)
                .build();
    }

    private static Type buildType() {
        return Type.builder()
                .pve(5)
                .pvp(6)
                .pvpRp(7)
                .rp(8)
                .build();
    }

    private static Region buildRegion() {
        return Region.builder()
                .eu(3)
                .us(4)
                .build();
    }

    private static Faction buildFaction() {
        return Faction.builder()
                .alliance(1)
                .horde(2)
                .build();
    }

    private ServerSummaryProjection getSummaryProjection() {
        return new ServerSummaryProjection() {
            @Override
            public int getAlliance() {
                return 1;
            }

            @Override
            public int getHorde() {
                return 2;
            }

            @Override
            public int getEu() {
                return 3;
            }

            @Override
            public int getUs() {
                return 4;
            }

            @Override
            public int getPve() {
                return 5;
            }

            @Override
            public int getPvp() {
                return 6;
            }

            @Override
            public int getPvpRp() {
                return 7;
            }

            @Override
            public int getRp() {
                return 8;
            }

            @Override
            public int getEnGB() {
                return 9;
            }

            @Override
            public int getEnUS() {
                return 10;
            }

            @Override
            public int getDeDE() {
                return 11;
            }

            @Override
            public int getEsES() {
                return 12;
            }

            @Override
            public int getFrFR() {
                return 13;
            }

            @Override
            public int getRuRU() {
                return 14;
            }

            @Override
            public int getTotal() {
                return 15;
            }
        };
    }
}