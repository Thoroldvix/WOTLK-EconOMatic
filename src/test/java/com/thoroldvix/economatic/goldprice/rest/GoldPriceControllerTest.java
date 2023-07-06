package com.thoroldvix.economatic.goldprice.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoroldvix.economatic.goldprice.dto.GoldPriceListResponse;
import com.thoroldvix.economatic.goldprice.dto.GoldPricePageResponse;
import com.thoroldvix.economatic.goldprice.dto.GoldPriceRequest;
import com.thoroldvix.economatic.goldprice.dto.GoldPriceResponse;
import com.thoroldvix.economatic.goldprice.service.GoldPriceService;
import com.thoroldvix.economatic.server.model.Faction;
import com.thoroldvix.economatic.server.model.Region;
import com.thoroldvix.economatic.shared.dto.PaginationInfo;
import com.thoroldvix.economatic.shared.dto.SearchCriteria;
import com.thoroldvix.economatic.shared.dto.SearchRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GoldPriceController.class)
@ActiveProfiles("test")
class GoldPriceControllerTest {

    private static final LocalDateTime UPDATED_AT = LocalDateTime.parse("2023-07-05T19:05:14.846761");

    private static final String GOLD_PRICE_API_ENDPOINT = "/wow-classic/api/v1/servers/prices";

    private final GoldPriceResponse goldPriceResponse1 = GoldPriceResponse.builder()
            .price(BigDecimal.valueOf(0.000823))
            .server("nethergarde-keep-horde")
            .updatedAt(UPDATED_AT)
            .build();

    private final GoldPriceResponse goldPriceResponse2 = GoldPriceResponse.builder()
            .price(BigDecimal.valueOf(0.000809))
            .server("giantstalker-horde")
            .updatedAt(UPDATED_AT.minusDays(1))
            .build();

    private final GoldPriceResponse goldPriceResponse3 = GoldPriceResponse.builder()
            .price(BigDecimal.valueOf(0.00099))
            .server("mograine-horde")
            .updatedAt(UPDATED_AT.minusDays(2))
            .build();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private GoldPriceService goldPriceService;

    @Test
    void getAll_returnsCorrectGoldPricePageResponse() throws Exception {
        String expectedJson = """
                {
                "page": 0,
                "pageSize": 100,
                "totalPages": 1,
                "totalElements": 3,
                "prices": [
                {
                "price": 0.000823,
                "server": "nethergarde-keep-horde",
                "updatedAt": "2023-07-05T19:05:14.846761"
                },
                {
                "price": 0.000809,
                "server": "giantstalker-horde",
                "updatedAt": "2023-07-04T19:05:14.846761"
                },
                {
                "price": 0.00099,
                "server": "mograine-horde",
                "updatedAt": "2023-07-03T19:05:14.846761"
                }
                ]
                }
                """;
        GoldPricePageResponse expected = buildGoldPricePageResponse(List.of(goldPriceResponse1, goldPriceResponse2, goldPriceResponse3));
        when(goldPriceService.getAll(any(), any())).thenReturn(expected);
        mockMvc.perform(get(GOLD_PRICE_API_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void getAllRecent_returnCorrectGoldPriceListResponse() throws Exception {
        String expectedJson = """
                {
                "prices": [{
                "price": 0.000823,
                "server": "nethergarde-keep-horde",
                "updatedAt": "2023-07-05T19:05:14.846761"
                },
                {
                "price": 0.000809,
                "server": "giantstalker-horde",
                "updatedAt": "2023-07-04T19:05:14.846761"
                },
                {
                "price": 0.00099,
                "server": "mograine-horde",
                "updatedAt": "2023-07-03T19:05:14.846761"
                }
                ]
                }
                """;
        GoldPriceListResponse expected = buildGoldPriceListResponse(List.of(goldPriceResponse1, goldPriceResponse2, goldPriceResponse3));
        when(goldPriceService.getAllRecent()).thenReturn(expected);

        mockMvc.perform(get(GOLD_PRICE_API_ENDPOINT + "/recent"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void getRecentForServers_returnsCorrectGoldPriceListResponse() throws Exception {
        String expectedJson = """
                {
                "prices": [{
                "price": 0.000823,
                "server": "nethergarde-keep-horde",
                "updatedAt": "2023-07-05T19:05:14.846761"
                },
                {
                "price": 0.000809,
                "server": "giantstalker-horde",
                "updatedAt": "2023-07-04T19:05:14.846761"
                },
                {
                "price": 0.00099,
                "server": "mograine-horde",
                "updatedAt": "2023-07-03T19:05:14.846761"
                }
                ]
                }
                """;
        GoldPriceListResponse expected = buildGoldPriceListResponse(List.of(goldPriceResponse1, goldPriceResponse2, goldPriceResponse3));
        GoldPriceRequest request = GoldPriceRequest.builder()
                .serverList(Set.of("nethergarde-keep-horde", "giantstalker-horde", "mograine-horde"))
                .build();
        when(goldPriceService.getRecentForServerList(request)).thenReturn(expected);
        mockMvc.perform(post(GOLD_PRICE_API_ENDPOINT + "/recent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void getForServer_returnsCorrectGoldPricePageResponse() throws Exception {
        String expectedJson = """
                {
                "page": 0,
                "pageSize": 100,
                "totalPages": 1,
                "totalElements": 1,
                "prices": [{
                "price": 0.000809,
                "server": "giantstalker-horde",
                "updatedAt": "2023-07-04T19:05:14.846761"
                }
                ]
                }
                """;
        GoldPricePageResponse expected = buildGoldPricePageResponse(Collections.singletonList(goldPriceResponse2));
        String serverName = "giantstalker-horde";

        when(goldPriceService.getForServer(any(), any(), any())).thenReturn(expected);
        mockMvc.perform(get(GOLD_PRICE_API_ENDPOINT + "/" + serverName))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void getRecentForServer_returnsCorrectGoldPriceResponse() throws Exception {
        String expectedJson = """            
                {
                "price": 0.000809,
                "server": "giantstalker-horde",
                "updatedAt": "2023-07-04T19:05:14.846761"
                }
                """;
        String serverName = "giantstalker-horde";
        when(goldPriceService.getRecentForServer(any())).thenReturn(goldPriceResponse2);
        mockMvc.perform(get(GOLD_PRICE_API_ENDPOINT + "/" + serverName + "/recent"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void search_returnsCorrectGoldPricePageResponse() throws Exception {
        String expectedJson = """            
                {
                "page": 0,
                "pageSize": 100,
                "totalPages": 1,
                "totalElements": 3,
                "prices": [
                {
                "price": 0.000823,
                "server": "nethergarde-keep-horde",
                "updatedAt": "2023-07-05T19:05:14.846761"
                },
                {
                "price": 0.000809,
                "server": "giantstalker-horde",
                "updatedAt": "2023-07-04T19:05:14.846761"
                },
                {
                "price": 0.00099,
                "server": "mograine-horde",
                "updatedAt": "2023-07-03T19:05:14.846761"
                }
                ]
                }
                """;
        GoldPricePageResponse expected = buildGoldPricePageResponse(List.of(goldPriceResponse1, goldPriceResponse2, goldPriceResponse3));
        SearchCriteria searchCriteria = buildSearchCriteria();
        SearchRequest request = buildSearchRequest(searchCriteria);

        when(goldPriceService.search(any(), any())).thenReturn(expected);

        mockMvc.perform(post(GOLD_PRICE_API_ENDPOINT + "/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));

    }

    @Test
    void getRecentForRegion_returnsCorrectGoldPriceListResponse() throws Exception {
        String expectedJson = """
                {
                "prices": [{
                "price": 0.000823,
                "server": "nethergarde-keep-horde",
                "updatedAt": "2023-07-05T19:05:14.846761"
                },
                {
                "price": 0.000809,
                "server": "giantstalker-horde",
                "updatedAt": "2023-07-04T19:05:14.846761"
                },
                {
                "price": 0.00099,
                "server": "mograine-horde",
                "updatedAt": "2023-07-03T19:05:14.846761"
                }
                ]
                }
                """;
        GoldPriceListResponse expected = buildGoldPriceListResponse(List.of(goldPriceResponse1, goldPriceResponse2, goldPriceResponse3));
        when(goldPriceService.getRecentForRegion(Region.EU.name())).thenReturn(expected);
        mockMvc.perform(get(GOLD_PRICE_API_ENDPOINT + "/regions/" + Region.EU.name()))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void getRecentForFaction_returnsCorrectGoldPriceListResponse() throws Exception {
        String expectedJson = """
                {
                "prices": [{
                "price": 0.000823,
                "server": "nethergarde-keep-horde",
                "updatedAt": "2023-07-05T19:05:14.846761"
                },
                {
                "price": 0.000809,
                "server": "giantstalker-horde",
                "updatedAt": "2023-07-04T19:05:14.846761"
                },
                {
                "price": 0.00099,
                "server": "mograine-horde",
                "updatedAt": "2023-07-03T19:05:14.846761"
                }
                ]
                }
                """;
        GoldPriceListResponse expected = buildGoldPriceListResponse(List.of(goldPriceResponse1, goldPriceResponse2, goldPriceResponse3));
        String factionName = Faction.HORDE.name();
        when(goldPriceService.getRecentForFaction(factionName)).thenReturn(expected);
        mockMvc.perform(get(GOLD_PRICE_API_ENDPOINT + "/factions/" + factionName))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    private static SearchRequest buildSearchRequest(SearchCriteria searchCriteria) {
        return SearchRequest.builder()
                .globalOperator(SearchRequest.GlobalOperator.AND)
                .searchCriteria(new SearchCriteria[]{searchCriteria})
                .build();
    }

    private static SearchCriteria buildSearchCriteria() {
        return SearchCriteria.builder()
                .column("region")
                .value("eu")
                .joinTable("server")
                .operation(SearchCriteria.Operation.EQUALS)
                .build();
    }
    private GoldPricePageResponse buildGoldPricePageResponse(List<GoldPriceResponse> prices) {
        PaginationInfo paginationInfo = new PaginationInfo(0, 100, 1, prices.size());
        return GoldPricePageResponse.builder()
                .prices(prices)
                .paginationInfo(paginationInfo)
                .build();
    }

      private GoldPriceListResponse buildGoldPriceListResponse(List<GoldPriceResponse> prices) {
        return GoldPriceListResponse.builder()
                .prices(prices)
                .build();
    }
}