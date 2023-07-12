package com.thoroldvix.economatic.goldprice.unit;

import com.thoroldvix.economatic.goldprice.dto.GoldPriceResponse;
import com.thoroldvix.economatic.stats.goldprice.GoldPriceStatResponse;
import com.thoroldvix.economatic.stats.goldprice.GoldPriceStatController;
import com.thoroldvix.economatic.stats.goldprice.GoldPriceStatsService;
import com.thoroldvix.economatic.server.Faction;
import com.thoroldvix.economatic.server.Region;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GoldPriceStatController.class)
@ActiveProfiles("test")
class GoldPriceStatControllerTest {

    private static final String GOLD_PRICE_STAT_API_ENDPOINT = "/wow-classic/api/v1/servers/prices/stats";
    private final LocalDateTime UPDATED_AT = LocalDateTime.parse("2023-07-05T19:05:14.846761");
    private final GoldPriceResponse min = GoldPriceResponse.builder()
            .price(BigDecimal.valueOf(0.000799))
            .server("ashbringer-alliance")
            .updatedAt(UPDATED_AT)
            .build();
    private final GoldPriceResponse max = GoldPriceResponse.builder()
            .price(BigDecimal.valueOf(0.00235))
            .server("flamegor-alliance")
            .updatedAt(UPDATED_AT)
            .build();

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GoldPriceStatsService goldPriceStatsService;

    @Test
    void getStatsForAll_returnsGoldPriceStatResponse() throws Exception {
        String expectedJson = """
                {
                    "mean": 0.001147,
                    "median": 0.00109,
                    "minimum": {
                        "price": 0.000799,
                        "server": "ashbringer-alliance",
                        "updatedAt": "2023-07-05T19:05:14.846761"
                    },
                    "maximum": {
                        "price": 0.00235,
                        "server": "flamegor-alliance",
                        "updatedAt": "2023-07-05T19:05:14.846761"
                    },
                    "count": 96
                }
                """;
        GoldPriceStatResponse expected = buildGoldPriceStatResponse(max, 96L);
        when(goldPriceStatsService.getForAll(any())).thenReturn(expected);
        mockMvc.perform(get(GOLD_PRICE_STAT_API_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void getStatsForServer_returnsGoldPriceStatResponse() throws Exception {
        String expectedJson = """
                {
                    "mean": 0.001147,
                    "median": 0.00109,
                    "minimum": {
                        "price": 0.000799,
                        "server": "ashbringer-alliance",
                        "updatedAt": "2023-07-05T19:05:14.846761"
                    },
                    "maximum": {
                        "price": 0.00235,
                        "server": "ashbringer-alliance",
                        "updatedAt": "2023-07-04T19:05:14.846761"
                    },
                    "count": 50
                }
                """;
        String serverName = "ashbringer-alliance";
        GoldPriceResponse max = GoldPriceResponse.builder()
                .updatedAt(UPDATED_AT.minusDays(1))
                .server(serverName)
                .price(BigDecimal.valueOf(0.00235))
                .build();
        GoldPriceStatResponse expected = buildGoldPriceStatResponse(max, 50L);
        when(goldPriceStatsService.getForServer(any(), any())).thenReturn(expected);
        mockMvc.perform(get(GOLD_PRICE_STAT_API_ENDPOINT + "/" + serverName))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void getStatsForRegion_returnsGoldPriceStatResponse() throws Exception {
        String expectedJson = """
                {
                    "mean": 0.001147,
                    "median": 0.00109,
                    "minimum": {
                        "price": 0.000799,
                        "server": "ashbringer-alliance",
                        "updatedAt": "2023-07-05T19:05:14.846761"
                    },
                    "maximum": {
                        "price": 0.00235,
                        "server": "flamegor-alliance",
                        "updatedAt": "2023-07-05T19:05:14.846761"
                    },
                    "count": 96
                }
                """;
        GoldPriceStatResponse expected = buildGoldPriceStatResponse(max, 96L);
        String regionName = Region.EU.name();
        when(goldPriceStatsService.getForRegion(any(), any())).thenReturn(expected);
        mockMvc.perform(get(GOLD_PRICE_STAT_API_ENDPOINT + "/regions/" + regionName))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    private GoldPriceStatResponse buildGoldPriceStatResponse(GoldPriceResponse max, long count) {
        return GoldPriceStatResponse.builder()
                .mean(BigDecimal.valueOf(0.001147))
                .median(BigDecimal.valueOf(0.00109))
                .minimum(min)
                .maximum(max)
                .count(count)
                .build();
    }

    @Test
    void getStatsForFaction_returnsGoldPriceStatResponse() throws Exception {
        String expectedJson = """
                {
                    "mean": 0.001147,
                    "median": 0.00109,
                    "minimum": {
                        "price": 0.000799,
                        "server": "ashbringer-alliance",
                        "updatedAt": "2023-07-05T19:05:14.846761"
                    },
                    "maximum": {
                        "price": 0.00235,
                        "server": "flamegor-alliance",
                        "updatedAt": "2023-07-05T19:05:14.846761"
                    },
                    "count": 96
                }
                """;
        GoldPriceStatResponse expected = buildGoldPriceStatResponse(max, 96L);
        String factionName = Faction.ALLIANCE.name();
        when(goldPriceStatsService.getForFaction(any(), any())).thenReturn(expected);
        mockMvc.perform(get(GOLD_PRICE_STAT_API_ENDPOINT + "/factions/" + factionName))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}