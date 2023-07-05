package com.thoroldvix.economatic.goldprice.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoroldvix.economatic.goldprice.dto.GoldPriceListResponse;
import com.thoroldvix.economatic.goldprice.dto.GoldPricePageResponse;
import com.thoroldvix.economatic.goldprice.dto.GoldPriceRequest;
import com.thoroldvix.economatic.goldprice.dto.GoldPriceResponse;
import com.thoroldvix.economatic.goldprice.service.GoldPriceService;
import com.thoroldvix.economatic.shared.dto.PaginationInfo;
import com.thoroldvix.economatic.shared.dto.TimeRange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private GoldPriceResponse goldPriceResponse1;
    private GoldPriceResponse goldPriceResponse2;
    private GoldPriceResponse goldPriceResponse3;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private GoldPriceService goldPriceService;

    @BeforeEach
    void setup() {
        goldPriceResponse1 = GoldPriceResponse.builder()
                .price(BigDecimal.valueOf(0.000823))
                .server("nethergarde-keep-horde")
                 .updatedAt(UPDATED_AT)
                .build();
        goldPriceResponse2 = GoldPriceResponse.builder()
                .price(BigDecimal.valueOf(0.000809))
                .server("giantstalker-horde")
                .updatedAt(UPDATED_AT.minusDays(1))
                .build();
        goldPriceResponse3 = GoldPriceResponse.builder()
                .price(BigDecimal.valueOf(0.00099))
                .server("mograine-horde")
                .updatedAt(UPDATED_AT.minusDays(2))
                .build();
    }

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
        PaginationInfo paginationInfo = new PaginationInfo(0, 100, 1, 3);
        GoldPricePageResponse expected = GoldPricePageResponse.builder()
                .prices(List.of(goldPriceResponse1, goldPriceResponse2, goldPriceResponse3))
                .paginationInfo(paginationInfo)
                .build();
        when(goldPriceService.getAll(any(TimeRange.class), any(Pageable.class))).thenReturn(expected);
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
        GoldPriceListResponse expected = GoldPriceListResponse.builder()
                .prices(List.of(goldPriceResponse1, goldPriceResponse2, goldPriceResponse3))
                .build();
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
        GoldPriceListResponse expected = GoldPriceListResponse.builder()
                .prices(List.of(goldPriceResponse1, goldPriceResponse2, goldPriceResponse3))
                .build();
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
}