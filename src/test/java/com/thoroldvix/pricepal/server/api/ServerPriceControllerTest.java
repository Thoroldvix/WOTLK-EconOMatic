package com.thoroldvix.pricepal.server.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoroldvix.pricepal.server.dto.ServerPriceResponse;
import com.thoroldvix.pricepal.server.entity.Currency;
import com.thoroldvix.pricepal.server.entity.Faction;
import com.thoroldvix.pricepal.server.entity.Region;
import com.thoroldvix.pricepal.server.service.ServerPriceService;
import com.vaadin.flow.router.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ServerPriceController.class)
@ActiveProfiles("test")
public class ServerPriceControllerTest {
    public static final String API_REALMS = "/wow-classic/api/v1/prices";


    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ServerPriceService serverPriceServiceImpl;


    @Test
    void getAllPricesForServer_returnsListOfServerPriceResponse() throws Exception {
        String serverName = "test-server";
        ServerPriceResponse firstServerPriceResponse = ServerPriceResponse.builder()
                .value(BigDecimal.valueOf(100))
                .build();
        ServerPriceResponse secondServerPriceResponse = ServerPriceResponse.builder()
                .value(BigDecimal.valueOf(200))
                .build();
        List<ServerPriceResponse> expectedResponse = List.of(firstServerPriceResponse, secondServerPriceResponse);

        when(serverPriceServiceImpl.getAllPricesForServer(anyString(), any(Pageable.class)))
                .thenReturn(expectedResponse);

        mockMvc.perform(get(API_REALMS + "/{serverName}", serverName))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)))
                .andReturn();
    }

    @Test
    void getAllPricesForServer_whenServerNameBlank_returnsBadRequest() throws Exception {
        String serverName = " ";

        when(serverPriceServiceImpl.getAllPricesForServer(anyString(), any(Pageable.class)))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(get(API_REALMS + "/{serverName}", serverName))
                .andExpect(status().isBadRequest());
    }
    @Test
    void getAvgPriceForRegion_whenRegionValid_returnsServerPriceResponse() throws Exception {
        String regionName = "eu";
        ServerPriceResponse price = ServerPriceResponse.builder()
                .value(BigDecimal.valueOf(100))
                .currency(Currency.USD)
                .lastUpdated(LocalDateTime.now())
                .build();

        when(serverPriceServiceImpl.getAvgPriceForRegion(Region.EU))
                .thenReturn(price);

        mockMvc.perform(get(API_REALMS + "/regions/{regionName}/avg", regionName))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(price)));
    }
    @Test
    void getAvgPriceForRegion_whenRegionBlank_returnsBadRequest() throws Exception  {
        String regionName = " ";

        when(serverPriceServiceImpl.getAvgPriceForRegion(any(Region.class)))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(get(API_REALMS + "/regions/{regionName}/avg", regionName))
                .andExpect(status().isBadRequest());
    }
    @Test
    void getAllPricesForRegion_whenRegionValid_returnsListOfServerPriceResponse() throws Exception {
        String regionName = "eu";
        ServerPriceResponse firstServerPriceResponse = ServerPriceResponse.builder()
                .value(BigDecimal.valueOf(100))
                .build();
        ServerPriceResponse secondServerPriceResponse = ServerPriceResponse.builder()
                .value(BigDecimal.valueOf(200))
                .build();
        List<ServerPriceResponse> expectedResponse = List.of(firstServerPriceResponse, secondServerPriceResponse);

        when(serverPriceServiceImpl.getAllPricesForRegion(any(Region.class), any(Pageable.class)))
                .thenReturn(expectedResponse);
        mockMvc.perform(get(API_REALMS + "/regions/{regionName}", regionName))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    void getAllPricesForRegion_whenRegionBlank_returnsBadRequest() throws Exception {
        String regionName = " ";
        when(serverPriceServiceImpl.getAllPricesForRegion(any(Region.class), any(Pageable.class)))
                .thenThrow(NotFoundException.class);
        mockMvc.perform(get(API_REALMS + "/regions/{regionName}", regionName))
                .andExpect(status().isBadRequest());
    }
    @Test
    void getAvgPriceForServer_whenServerNameValid_returnsServerPriceResponse() throws Exception {
        String serverName = "test-server";
        ServerPriceResponse price = ServerPriceResponse.builder()
                .value(BigDecimal.valueOf(100))
                .currency(Currency.USD)
                .lastUpdated(LocalDateTime.now())
                .build();

        when(serverPriceServiceImpl.getAvgPriceForServer(anyString()))
                .thenReturn(price);

        mockMvc.perform(get(API_REALMS + "/{serverName}/avg", serverName))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(price)));
    }

    @Test
    void getAvgPriceForServer_whenServerNameBlank_returnsBadRequest() throws Exception {
        String serverName = " ";
        when(serverPriceServiceImpl.getAvgPriceForServer(anyString()))
                .thenThrow(NotFoundException.class);
        mockMvc.perform(get(API_REALMS + "/{serverName}/avg", serverName))
                .andExpect(status().isBadRequest());
    }
    @Test
    void getAllPricesForFaction_whenFactionValid_returnsListOfServerPriceResponse() throws Exception {
        String factionName = "horde";
        ServerPriceResponse firstServerPriceResponse = ServerPriceResponse.builder()
                .value(BigDecimal.valueOf(100))
                .build();
        ServerPriceResponse secondServerPriceResponse = ServerPriceResponse.builder()
                .value(BigDecimal.valueOf(200))
                .build();
        List<ServerPriceResponse> expectedResponse = List.of(firstServerPriceResponse, secondServerPriceResponse);

        when(serverPriceServiceImpl.getAllPricesForFaction(any(Faction.class), any(Pageable.class)))
                .thenReturn(expectedResponse);

        mockMvc.perform(get(API_REALMS + "/factions/{factionName}", factionName))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }
    @Test
    void getAllPricesForFaction_whenFactionBlank_returnsBadRequest() throws Exception {
        String factionName = " ";
        when(serverPriceServiceImpl.getAllPricesForFaction(any(Faction.class), any(Pageable.class)))
                .thenThrow(NotFoundException.class);
        mockMvc.perform(get(API_REALMS + "/factions/{factionName}", factionName))
                .andExpect(status().isBadRequest());
    }
    @Test
    void getAvgPriceForFaction_whenFactionValid_returnsServerPriceResponse() throws Exception {
        String factionName = "horde";
        ServerPriceResponse price = ServerPriceResponse.builder()
                .value(BigDecimal.valueOf(100))
                .currency(Currency.USD)
                .lastUpdated(LocalDateTime.now())
                .build();

        when(serverPriceServiceImpl.getAvgPriceForFaction(Faction.HORDE))
                .thenReturn(price);

        mockMvc.perform(get(API_REALMS + "/factions/{factionName}/avg", factionName))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(price)));
    }
    @Test
    void getAvgPriceForFaction_whenFactionBlank_returnsBadRequest() throws Exception {
        String factionName = " ";
        when(serverPriceServiceImpl.getAvgPriceForFaction(Faction.HORDE))
                .thenThrow(NotFoundException.class);
        mockMvc.perform(get(API_REALMS + "/factions/{factionName}/avg", factionName))
                .andExpect(status().isBadRequest());
    }


}