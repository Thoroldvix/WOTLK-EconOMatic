package com.thoroldvix.pricepal.price;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoroldvix.pricepal.server.api.PriceController;
import com.thoroldvix.pricepal.server.dto.ServerPriceResponse;
import com.thoroldvix.pricepal.server.service.ServerPriceService;
import com.thoroldvix.pricepal.server.service.ServerService;
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
public class ServerPriceControllerTest {

    public static final String API_REALMS = "/wow-classic/api/v1/prices";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ServerPriceService classicServerPriceService;



    @MockBean
    private ServerService serverServiceImpl;


    @Test
    void getPriceForRealm_whenValidRealmData_returnsPriceResponse() throws Exception {
        String serverName = "test-server";
        ServerPriceResponse serverPriceResponse = ServerPriceResponse.builder()
                .value(BigDecimal.valueOf(100))
                .build();

        when(classicServerPriceService.getPriceForServer((serverName))).thenReturn(serverPriceResponse);

        mockMvc.perform(get(API_REALMS + "/{serverName}", serverName))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(serverPriceResponse)));

        verify(classicServerPriceService).getPriceForServer(serverName);
    }


    @Test
    void getPriceForRealm_whenServerNameInvalid_returnsNotFound() throws Exception {
        String serverName = "invalid";

        when(classicServerPriceService.getPriceForServer(serverName)).thenThrow(NotFoundException.class);

        mockMvc.perform(get(API_REALMS + "/{serverName}", serverName))
                .andExpect(status().isNotFound());


    }
        @Test
    void getAllPricesForRealm_whenServerNameInvalid_returnsNotFound() throws Exception {
        String serverName = "invalid";

        when(classicServerPriceService.getAllPricesForServer(anyString(), any(Pageable.class))).thenThrow(NotFoundException.class);

        mockMvc.perform(get(API_REALMS + "/{serverName}/all", serverName))
                .andExpect(status().isNotFound());


    }


    @Test
    void getAllPricesForRealm_returnsListOfPriceResponse() throws Exception {
        String serverName = "test-server";
        ServerPriceResponse firstServerPriceResponse = ServerPriceResponse.builder()
                .value(BigDecimal.valueOf(100))
                .build();
        ServerPriceResponse secondServerPriceResponse = ServerPriceResponse.builder()
                .value(BigDecimal.valueOf(200))
                .build();
        List<ServerPriceResponse> expectedResponse = List.of(firstServerPriceResponse, secondServerPriceResponse);

        when(classicServerPriceService.getAllPricesForServer(anyString(), any(Pageable.class))).thenReturn(expectedResponse);

        mockMvc.perform(get(API_REALMS + "/{serverName}/all", serverName))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)))
                .andReturn();
    }

}