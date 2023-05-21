package com.thoroldvix.g2gcalculator.price;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoroldvix.g2gcalculator.server.api.PriceController;
import com.thoroldvix.g2gcalculator.server.dto.ServerPrice;
import com.thoroldvix.g2gcalculator.server.service.PriceService;
import com.thoroldvix.g2gcalculator.server.service.ServerService;
import com.vaadin.flow.router.NotFoundException;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    private ServerService serverServiceImpl;


    @Test
    void getPriceForRealm_whenValidRealmData_returnsPriceResponse() throws Exception {
        String serverName = "test-server";
        ServerPrice serverPrice = ServerPrice.builder()
                .value(BigDecimal.valueOf(100))
                .build();

        when(classicPriceService.getPriceForServer((serverName))).thenReturn(serverPrice);

        mockMvc.perform(get(API_REALMS + "/{serverName}", serverName))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(serverPrice)));

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
        ServerPrice firstServerPrice = ServerPrice.builder()
                .value(BigDecimal.valueOf(100))
                .build();
        ServerPrice secondServerPrice = ServerPrice.builder()
                .value(BigDecimal.valueOf(200))
                .build();
        List<ServerPrice> expectedResponse = List.of(firstServerPrice, secondServerPrice);

        when(classicPriceService.getAllPricesForServer(anyString(), any(Pageable.class))).thenReturn(expectedResponse);

        mockMvc.perform(get(API_REALMS + "/{serverName}/all", serverName))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)))
                .andReturn();
    }

}