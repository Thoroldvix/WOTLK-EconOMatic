package com.thoroldvix.pricepal.server.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoroldvix.pricepal.server.dto.ServerPriceResponse;
import com.thoroldvix.pricepal.server.service.ServerPriceService;
import org.junit.internal.requests.FilterRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;

import static com.thoroldvix.pricepal.server.service.ServerPriceServiceTest.getServerPriceResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ServerPriceController.class)
@ActiveProfiles("test")
public class ServerPriceControllerTest {
    public static final String API_PRICES = "/wow-classic/api/v1/prices";
    private static FilterRequest filters;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ServerPriceService serverPriceService;

    @BeforeAll
    public static void setUp() {
        filters = FilterRequest.builder()
                .region("eu")
                .faction("horde")
                .serverName("everlook")
                .timeRange(7)
                .uniqueServerName("everlook-horde")
                .build();
    }

    private static MockHttpServletRequestBuilder getRequestWithParams(String x) {
        return MockMvcRequestBuilders.get(API_PRICES + x)
                .param("region", "eu")
                .param("faction", "horde")
                .param("serverName", "everlook")
                .param("timeRange", "7")
                .param("uniqueServerName", "everlook-horde");
    }

    private FilteredPriceResponse getFilteredPriceResponse(Consumer<FilteredPriceResponse.FilteredPriceResponseBuilder> responseBuilderConsumer) {
        FilteredPriceResponse.FilteredPriceResponseBuilder responseBuilder = FilteredPriceResponse.builder()
                .filters(filters);
        responseBuilderConsumer.accept(responseBuilder);
        return responseBuilder.build();
    }
    @Test
    void getFilteredPrices_whenFiltersProvided_returnsCorrectFilteredPriceResponse() throws Exception {
        List<ServerPriceResponse> prices = List.of(getServerPriceResponse(BigDecimal.TEN, Currency.USD));
        FilteredPriceResponse expectedResponse = getFilteredPriceResponse(builder -> builder.prices(prices));

        when(serverPriceService.searchForPrices(any(),  any())).thenReturn(prices);

        mockMvc.perform(getRequestWithParams(""))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }


    @Test
    void getAvgPrice_whenFiltersProvided_returnsCorrectFilteredPriceResponse() throws Exception {
        ServerPriceResponse avgPrice = getServerPriceResponse(BigDecimal.TEN, Currency.USD);
        FilteredPriceResponse expectedResponse = getFilteredPriceResponse(builder -> builder.avgPrice(avgPrice));

        when(serverPriceService.getAvgPrices(any())).thenReturn(avgPrice);

        mockMvc.perform(getRequestWithParams("/avg"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }


    @Test
    void getMaxPrice_whenFiltersProvided_returnsCorrectFilteredPriceResponse() throws Exception {
        ServerPriceResponse maxPrice = getServerPriceResponse(BigDecimal.TEN, Currency.USD);
        FilteredPriceResponse expectedResponse = getFilteredPriceResponse(builder -> builder.maxPrice(maxPrice));

        when(serverPriceService.getMaxPrices(any())).thenReturn(maxPrice);

        mockMvc.perform(getRequestWithParams("/max"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }
    @Test
    void getMinPrice_whenFiltersProvided_returnsCorrectFilteredPriceResponse() throws Exception {
        ServerPriceResponse minPrice = getServerPriceResponse(BigDecimal.TEN, Currency.USD);
        FilteredPriceResponse expectedResponse = getFilteredPriceResponse(builder -> builder.minPrice(minPrice));

        when(serverPriceService.getMinPrices(any())).thenReturn(minPrice);

        mockMvc.perform(getRequestWithParams("/min"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }
    @Test
    void getMedianPrice_whenFiltersProvided_returnsCorrectFilteredPriceResponse() throws Exception {
        ServerPriceResponse medianPrice = getServerPriceResponse(BigDecimal.TEN, Currency.USD);
        FilteredPriceResponse expectedResponse = getFilteredPriceResponse(builder -> builder.medianPrice(medianPrice));

        when(serverPriceService.getMedianPrices(any())).thenReturn(medianPrice);

        mockMvc.perform(getRequestWithParams("/med"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }


}