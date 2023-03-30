package com.thoroldvix.g2gcalculator.api;

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

@WebMvcTest(ClassicRealmController.class)
@ActiveProfiles("test")
class ClassicServerControllerTest {
    public static final String API_REALMS = "/wow-classic/v1/realms";
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ServerService classicServerService;

    @Test
    void getAllRealms_returnsListOfRealmResponse() throws Exception {
        ServerResponse firstServerResponse = ServerResponse.builder()
                .id(1)
                .name("test-server")
                .build();
        ServerResponse secondServerResponse = ServerResponse.builder()
                .id(2)
                .name("test-server")
                .build();
        List<ServerResponse> expectedRealms = List.of(firstServerResponse, secondServerResponse);

        when(classicServerService.getAllServers(any(Pageable.class))).thenReturn(expectedRealms);

        mockMvc.perform(get(API_REALMS))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedRealms)));
    }
    @Test
    void getRealm_whenRealmNameValid_returnsRealmResponse() throws Exception {
        String realmName = "test-server";
        ServerResponse expectedResponse = ServerResponse.builder()
                .id(1)
                .name(realmName)
                .build();

        when(classicServerService.getServerResponse(realmName)).thenReturn(expectedResponse);

        mockMvc.perform(get(API_REALMS + "/{realmName}", realmName))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));

    }
    @Test
    void getRealm_whenRealmNameInvalid_returnsNotFound() throws Exception {
        String realmName = "test";

        when(classicServerService.getServerResponse(realmName)).thenThrow(NotFoundException.class);
        mockMvc.perform(get(API_REALMS + "/{realmName}", realmName))
                .andExpect(status().isNotFound());

    }
}