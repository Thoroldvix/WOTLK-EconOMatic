package com.thoroldvix.g2gcalculator.server;

import com.thoroldvix.g2gcalculator.server.ServerController;
import com.thoroldvix.g2gcalculator.server.ServerResponse;
import com.thoroldvix.g2gcalculator.common.NotFoundException;
import com.thoroldvix.g2gcalculator.server.ServerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
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
class ClassicServerControllerTest {
    public static final String API_SERVERS = "/wow-classic/v1/servers";
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ServerService classicServerService;

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

        when(classicServerService.getAllServers(any(Pageable.class))).thenReturn(expectedServers);

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

        when(classicServerService.getServerResponse(serverName)).thenReturn(expectedResponse);

        mockMvc.perform(get(API_SERVERS + "/{serverName}", serverName))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));

    }
    @Test
    void getServer_whenServerNameInvalid_returnsNotFound() throws Exception {
        String serverName = "test";

        when(classicServerService.getServerResponse(serverName)).thenThrow(NotFoundException.class);
        mockMvc.perform(get(API_SERVERS + "/{serverName}", serverName))
                .andExpect(status().isNotFound());

    }
}