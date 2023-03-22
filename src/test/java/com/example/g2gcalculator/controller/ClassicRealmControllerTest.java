package com.example.g2gcalculator.controller;


import com.example.g2gcalculator.dto.RealmResponse;
import com.example.g2gcalculator.service.RealmService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClassicRealmController.class)
@ActiveProfiles("test")
public class ClassicRealmControllerTest {

    public static final String API_V_1_REALMS_CLASSIC = "/api/v1/realms/classic";
    public static final String API_V_1_REALMS_RETAIL = "/api/v1/realms/retail";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RealmService realmService;

    @Test
    void getAllRealms_shouldWork() throws Exception {
        RealmResponse everlook = RealmResponse.builder()
                .name("Everlook")
                .build();
         RealmResponse gehenas = RealmResponse.builder()
                .name("Gehenas")
                .build();

        List<RealmResponse> realms = List.of(everlook,
                gehenas);

        when(realmService.getAllRealms()).thenReturn(realms);

        MvcResult mvcResult = mockMvc.perform(get(API_V_1_REALMS_CLASSIC))
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();

        assertThat(contentAsString).isNotBlank();

        List<RealmResponse> result = objectMapper.readValue(contentAsString,
                objectMapper.getTypeFactory().constructCollectionType(List.class, RealmResponse.class));

        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(realms);
    }
}