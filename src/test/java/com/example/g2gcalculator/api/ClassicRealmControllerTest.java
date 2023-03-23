package com.example.g2gcalculator.api;

import com.example.g2gcalculator.dto.RealmResponse;
import com.example.g2gcalculator.service.RealmService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Comparator;
import java.util.List;

import static com.example.g2gcalculator.util.TestUtil.createRealmResponseList;
import static org.assertj.core.api.Assertions.assertThat;
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
        List<RealmResponse> expectedRealms = createRealmResponseList(10);

        when(classicRealmService.getAllRealms(any(Pageable.class))).thenReturn(expectedRealms);

        MvcResult mvcResult = mockMvc.perform(get(API_REALMS))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedRealms)))
                .andReturn();

        List<RealmResponse> actualRealms = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<RealmResponse>>() {
                });

        assertThat(actualRealms).hasSize(10);
        assertThat(actualRealms).isEqualTo(expectedRealms);
    }

    @Test
    public void getAllRealms_returnsCorrectPageSize() throws Exception {
        int page = 0;
        int size = 5;
        String sort = "id,asc";

        when(classicRealmService.getAllRealms(any(Pageable.class))).thenReturn(createRealmResponseList(size));

        MvcResult result = mockMvc.perform(get(API_REALMS + "?page={page}&size={size}&sort={sort}", page, size, sort))
                .andExpect(status().isOk())
                .andReturn();


        List<RealmResponse> realms = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertThat(realms.size()).isEqualTo(size);
    }
    @Test
    public void getAllRealms_returnsCorrectlySortedList() throws Exception {
        String sort = "id,desc";

        List<RealmResponse> expectedResponse = createRealmResponseList(5);

        expectedResponse.sort(Comparator.comparing(RealmResponse::id).reversed());


        when(classicRealmService.getAllRealms(any(Pageable.class))).thenReturn(expectedResponse);

        MvcResult result = mockMvc.perform(get(API_REALMS + "?sort={sort}", sort))
                .andExpect(status().isOk())
                .andReturn();


        List<RealmResponse> actualResponse = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertThat(actualResponse.size()).isEqualTo(5);
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }
}