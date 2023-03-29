package com.thoroldvix.g2gcalculator.api;


import com.thoroldvix.g2gcalculator.dto.ItemPriceResponse;
import com.thoroldvix.g2gcalculator.dto.PriceResponse;
import com.thoroldvix.g2gcalculator.error.NotFoundException;
import com.thoroldvix.g2gcalculator.service.ItemPriceService;
import com.thoroldvix.g2gcalculator.service.PriceService;
import com.thoroldvix.g2gcalculator.service.RealmService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClassicPriceController.class)
@ActiveProfiles("test")
public class ClassicPriceControllerTest {

    public static final String API_REALMS = "/wow-classic/v1/prices";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private PriceService classicPriceService;

    @MockBean
    private ItemPriceService classicItemPriceService;

    @MockBean
    private RealmService classicRealmService;


    @Test
    void getPriceForRealm_whenValidRealmData_returnsPriceResponse() throws Exception {
        String realmName = "test-realm";
        PriceResponse priceResponse = PriceResponse.builder()
                .value(BigDecimal.valueOf(100))
                .build();

        when(classicPriceService.getPriceForRealmName(realmName)).thenReturn(priceResponse);

        mockMvc.perform(get(API_REALMS + "/{realmName}", realmName))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(priceResponse)));

        verify(classicPriceService).getPriceForRealmName(realmName);
    }


    @Test
    void getPriceForRealm_whenRealmNameInvalid_returnsNotFound() throws Exception {
        String realmName = "invalid";

        when(classicPriceService.getPriceForRealmName(realmName)).thenThrow(NotFoundException.class);

        mockMvc.perform(get(API_REALMS + "/{realmName}", realmName))
                .andExpect(status().isNotFound());


    }
        @Test
    void getAllPricesForRealm_whenRealmNameInvalid_returnsNotFound() throws Exception {
        String realmName = "invalid";

        when(classicPriceService.getAllPricesForRealm(anyString(), any(Pageable.class))).thenThrow(NotFoundException.class);

        mockMvc.perform(get(API_REALMS + "/{realmName}/all", realmName))
                .andExpect(status().isNotFound());


    }


    @Test
    void getAllPricesForRealm_returnsListOfPriceResponse() throws Exception {
        String realmName = "test-realm";
        PriceResponse firstPriceResponse = PriceResponse.builder()
                .value(BigDecimal.valueOf(100))
                .build();
        PriceResponse secondPriceResponse = PriceResponse.builder()
                .value(BigDecimal.valueOf(200))
                .build();
        List<PriceResponse> expectedResponse = List.of(firstPriceResponse, secondPriceResponse);

        when(classicPriceService.getAllPricesForRealm(anyString(), any(Pageable.class))).thenReturn(expectedResponse);

        mockMvc.perform(get(API_REALMS + "/{realmName}/all", realmName))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)))
                .andReturn();
    }

    @Test
    void getPriceForItem_whenValidRealmNameAndItemId_returnsItemPriceResponse() throws Exception {
        String realmName = "test-realm";
        int itemId = 123;
        BigDecimal price = BigDecimal.valueOf(50.0);
        ItemPriceResponse itemPrice = ItemPriceResponse.builder()
                .price(price)
                .build();
        when(classicItemPriceService.getPriceForItem(anyString(), anyInt(),anyInt(), anyBoolean()))
                .thenReturn(itemPrice);

        mockMvc.perform(get(API_REALMS + "/{realmName}/items/{itemId}", realmName, itemId))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(itemPrice)));
    }


    @Test
    void getPriceForItem_whenInvalidRealmName_returnsNotFound() throws Exception {
        String realmName = "invalid";
        int itemId = 123;
        int amount = 1;
        boolean minBuyout = false;

        when(classicItemPriceService.getPriceForItem(realmName, itemId, amount, minBuyout)).thenThrow(NotFoundException.class);

        mockMvc.perform(get(API_REALMS + "/{realmName}/items/{itemId}", realmName, itemId))
                .andExpect(status().isNotFound());


    }
}