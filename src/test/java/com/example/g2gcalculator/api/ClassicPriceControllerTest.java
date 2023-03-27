package com.example.g2gcalculator.api;


import com.example.g2gcalculator.dto.ItemPriceResponse;
import com.example.g2gcalculator.dto.PriceResponse;
import com.example.g2gcalculator.error.ApiError;
import com.example.g2gcalculator.error.NotFoundException;
import com.example.g2gcalculator.service.PriceService;
import com.example.g2gcalculator.service.RealmService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
    private RealmService classicRealmService;


    @Test
    void getPriceForRealm_whenValidRealmData_returnsPriceResponse() throws Exception {
        String realmName = "test-realm";
        PriceResponse priceResponse = PriceResponse.builder()
                .value(BigDecimal.valueOf(100))
                .build();

        when(classicPriceService.getPriceForRealm(realmName)).thenReturn(priceResponse);

        mockMvc.perform(get(API_REALMS + "/{realmName}", realmName))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(priceResponse)));

        verify(classicPriceService).getPriceForRealm(realmName);
    }


    @Test
    void getPriceForRealm_whenRealmNameInvalid_returnsNotFound() throws Exception {
        String realmName = "invalid";
        mockMvc.perform(get(API_REALMS + "/{realmName}", realmName))
                .andExpect(status().isNotFound());

        verifyNoInteractions(classicPriceService);
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
        when(classicPriceService.getPriceForItem(anyString(), anyInt(),anyInt(), anyBoolean()))
                .thenReturn(itemPrice);

        mockMvc.perform(get(API_REALMS + "/{realmName}/items/{itemId}", realmName, itemId))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(itemPrice)));
    }


    @Test
    void getPriceForItem_whenInvalidRealmName_returnsNotFound() throws Exception {
        String realmName = "invalid";
        int itemId = 123;

        mockMvc.perform(get(API_REALMS + "/{realmName}/items/{itemId}", realmName, itemId))
                .andExpect(status().isNotFound());

        verifyNoInteractions(classicPriceService);
    }
}