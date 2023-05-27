package com.thoroldvix.pricepal.server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoroldvix.pricepal.server.dto.ServerResponse;
import com.thoroldvix.pricepal.server.entity.Region;
import com.thoroldvix.pricepal.server.service.ServerService;
import com.vaadin.flow.router.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ServerController.class)
@ActiveProfiles("test")
class ServerControllerTest {
    public static final String API_SERVERS = "/wow-classic/api/v1/servers";
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ServerService serverServiceImpl;

    @Test
    void getAllServers_returnsListOfServerResponse() throws Exception {
        ServerResponse firstServerResponse = ServerResponse.builder()
                .id(1)
                .name("test-server")
                .build();
        ServerResponse secondServerResponse = ServerResponse.builder()
                .id(2)
                .name("test-server")
                .build();
        List<ServerResponse> expectedServers = List.of(firstServerResponse, secondServerResponse);

        when(serverServiceImpl.getAllServers()).thenReturn(expectedServers);

        mockMvc.perform(get(API_SERVERS))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedServers)));
    }

    @Test
    void getServer_whenServerNameValid_returnsServerResponse() throws Exception {
        String serverName = "test-server";
        ServerResponse expectedResponse = ServerResponse.builder()
                .id(1)
                .name(serverName)
                .build();

        when(serverServiceImpl.getServerResponse(serverName)).thenReturn(expectedResponse);

        mockMvc.perform(get(API_SERVERS + "/{serverName}", serverName))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));

    }

    @Test
    void getServer_whenServerNameBlank_returnsBadRequest() throws Exception {
        String serverName = " ";

        when(serverServiceImpl.getServerResponse(serverName)).thenThrow(NotFoundException.class);

        mockMvc.perform(get(API_SERVERS + "/{serverName}", serverName))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllServersForRegion_whenCalledWithValidRegion_returnsListOfServerResponse() throws Exception {
        ServerResponse firstServerResponse = ServerResponse.builder()
                .id(1)
                .region(Region.EU)
                .name("test-server")
                .build();
        ServerResponse secondServerResponse = ServerResponse.builder()
                .id(2)
                .region(Region.EU)
                .name("test-server")
                .build();

        List<ServerResponse> expectedServers = List.of(firstServerResponse, secondServerResponse);


        when(serverServiceImpl.getAllServersForRegion(Region.EU)).thenReturn(expectedServers);


        mockMvc.perform(get(API_SERVERS + "/regions/{regions}", "eu"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedServers)));
    }

    @Test
    void getAllServersForRegion_whenCalledWithInvalidRegion_returnsBadRequestResponse() throws Exception {
        ServerResponse firstServerResponse = ServerResponse.builder()
                .id(1)
                .region(Region.EU)
                .name("test-server")
                .build();
        ServerResponse secondServerResponse = ServerResponse.builder()
                .id(2)
                .region(Region.EU)
                .name("test-server")
                .build();
        ServerResponse thirdServerResponse = ServerResponse.builder()
                .id(3)
                .region(Region.US)
                .name("test-server")
                .build();
        List<ServerResponse> expectedServers = List.of(firstServerResponse, secondServerResponse, thirdServerResponse);

        when(serverServiceImpl.getAllServersForRegion(any())).thenReturn(expectedServers);


        mockMvc.perform(get(API_SERVERS + "/regions/{regions}", "e"))
                .andExpect(status().isBadRequest());

    }

}