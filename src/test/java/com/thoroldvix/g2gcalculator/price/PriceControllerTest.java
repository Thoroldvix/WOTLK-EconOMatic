package com.thoroldvix.g2gcalculator.price;


import com.thoroldvix.g2gcalculator.item.ItemPriceResponse;
import com.thoroldvix.g2gcalculator.common.NotFoundException;
import com.thoroldvix.g2gcalculator.item.ItemPriceService;
import com.thoroldvix.g2gcalculator.server.ServerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PriceController.class)
@ActiveProfiles("test")
public class PriceControllerTest {

    public static final String API_REALMS = "/wow-classic/api/v1/prices";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private PriceService classicPriceService;

    @MockBean
    private ItemPriceService classicItemPriceService;

    @MockBean
    private ServerService classicServerService;


    @Test
    void getPriceForRealm_whenValidRealmData_returnsPriceResponse() throws Exception {
        String serverName = "test-server";
        PriceResponse priceResponse = PriceResponse.builder()
                .value(BigDecimal.valueOf(100))
                .build();

        when(classicPriceService.getPriceForServer((serverName))).thenReturn(priceResponse);

        mockMvc.perform(get(API_REALMS + "/{serverName}", serverName))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(priceResponse)));

        verify(classicPriceService).getPriceForServer(serverName);
    }


    @Test
    void getPriceForRealm_whenServerNameInvalid_returnsNotFound() throws Exception {
        String serverName = "invalid";

        when(classicPriceService.getPriceForServer(serverName)).thenThrow(NotFoundException.class);

        mockMvc.perform(get(API_REALMS + "/{serverName}", serverName))
                .andExpect(status().isNotFound());


    }
        @Test
    void getAllPricesForRealm_whenServerNameInvalid_returnsNotFound() throws Exception {
        String serverName = "invalid";

        when(classicPriceService.getAllPricesForServer(anyString(), any(Pageable.class))).thenThrow(NotFoundException.class);

        mockMvc.perform(get(API_REALMS + "/{serverName}/all", serverName))
                .andExpect(status().isNotFound());


    }


    @Test
    void getAllPricesForRealm_returnsListOfPriceResponse() throws Exception {
        String serverName = "test-server";
        PriceResponse firstPriceResponse = PriceResponse.builder()
                .value(BigDecimal.valueOf(100))
                .build();
        PriceResponse secondPriceResponse = PriceResponse.builder()
                .value(BigDecimal.valueOf(200))
                .build();
        List<PriceResponse> expectedResponse = List.of(firstPriceResponse, secondPriceResponse);

        when(classicPriceService.getAllPricesForServer(anyString(), any(Pageable.class))).thenReturn(expectedResponse);

        mockMvc.perform(get(API_REALMS + "/{serverName}/all", serverName))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)))
                .andReturn();
    }

    @Test
    void getPriceForItem_whenValidServerNameAndItemId_returnsItemPriceResponse() throws Exception {
        String serverName = "test-server";
        int itemId = 123;
        BigDecimal price = BigDecimal.valueOf(50.0);
        ItemPriceResponse itemPrice = ItemPriceResponse.builder()
                .value(price)
                .build();
        when(classicItemPriceService.getPriceForItem(anyString(), anyString() ,anyInt(), anyBoolean()))
                .thenReturn(itemPrice);

        mockMvc.perform(get(API_REALMS + "/{serverName}/{itemId}", serverName, itemId))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(itemPrice)));
    }


    @Test
    void getPriceForItem_whenInvalidServerName_returnsNotFound() throws Exception {
        String serverName = "invalid";
        String itemId = "123";
        int amount = 1;
        boolean minBuyout = false;

        when(classicItemPriceService.getPriceForItem(serverName, itemId, amount, minBuyout)).thenThrow(NotFoundException.class);

        mockMvc.perform(get(API_REALMS + "/{serverName}/items/{itemId}", serverName, itemId))
                .andExpect(status().isNotFound());


    }
}