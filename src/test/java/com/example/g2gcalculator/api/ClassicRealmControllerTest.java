package com.example.g2gcalculator.api;

import com.example.g2gcalculator.dto.RealmResponse;
import com.example.g2gcalculator.error.ApiError;
import com.example.g2gcalculator.service.RealmService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
class ClassicRealmControllerTest {
    public static final String API_REALMS = "/wow-classic/v1/realms";
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RealmService classicRealmService;

    @Test
    void getAllRealms_returnsListOfRealmResponse() throws Exception {
        RealmResponse firstRealmResponse = RealmResponse.builder()
                .id(1)
                .name("test-realm")
                .build();
        RealmResponse secondRealmResponse = RealmResponse.builder()
                .id(2)
                .name("test-realm")
                .build();
        List<RealmResponse> expectedRealms = List.of(firstRealmResponse, secondRealmResponse);

        when(classicRealmService.getAllRealms(any(Pageable.class))).thenReturn(expectedRealms);

        mockMvc.perform(get(API_REALMS))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedRealms)));
    }
    @Test
    void getRealm_whenRealmNameValid_returnsRealmResponse() throws Exception {
        String realmName = "test-realm";
        RealmResponse expectedResponse = RealmResponse.builder()
                .id(1)
                .name(realmName)
                .build();

        when(classicRealmService.getRealmResponse(realmName)).thenReturn(expectedResponse);

        mockMvc.perform(get(API_REALMS + "/{realmName}", realmName))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));

    }
    @Test
    void getRealm_whenRealmNameInvalid_returnsNotFound() throws Exception {
        String realmName = "test";
        mockMvc.perform(get(API_REALMS + "/{realmName}", realmName))
                .andExpect(status().isNotFound());

    }
}