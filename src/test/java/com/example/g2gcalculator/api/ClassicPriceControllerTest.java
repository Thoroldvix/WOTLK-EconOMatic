package com.example.g2gcalculator.api;


import com.example.g2gcalculator.dto.PriceResponse;
import com.example.g2gcalculator.error.ApiError;
import com.example.g2gcalculator.error.NotFoundException;
import com.example.g2gcalculator.model.Realm;
import com.example.g2gcalculator.service.PriceService;
import com.example.g2gcalculator.service.RealmService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;

import static com.example.g2gcalculator.util.TestUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        Realm realm = createRealm();
        PriceResponse priceResponse = createPriceResponse(BigDecimal.valueOf(100));
        String url = getPriceForRealmUrl(realm);
        when(classicPriceService.getPriceForRealm(realm.getName() + "-" + realm.getFaction())).thenReturn(priceResponse);


        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(priceResponse)));
    }



    @Test
    void getPriceForRealm_whenFactionInvalid_returnsApiErrorWithNotFound() throws Exception {
        Realm realm = createRealm();
        realm.setFaction(null);
        ApiError expectedError = new ApiError(HttpStatus.NOT_FOUND.value(),"Not found" );
        String url = getPriceForRealmUrl(realm);


        when(classicPriceService.getPriceForRealm(anyString())).thenThrow(new NotFoundException("Not found"));
        MvcResult mvcResult = mockMvc.perform(get(url))
                .andExpect(status().isNotFound())
                .andReturn();

        ApiError apiError = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ApiError.class);
        assertThat(apiError.getMessage()).isEqualTo(expectedError.getMessage());
    }


    @Test
    void getAllPricesForRealm_returnsListOfPriceResponse() throws Exception {
        Realm realm = createRealm();
        List<PriceResponse> expectedResponse = createPriceResponseList(10);
        String url = getAllPricesForRealmUrl(realm);



        when(classicPriceService.getAllPricesForRealm(anyString(), any(Pageable.class))).thenReturn(expectedResponse);

        MvcResult mvcResult = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)))
                .andReturn();

        List<PriceResponse> actualResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
               new TypeReference<List<PriceResponse>>() {});

        assertThat(actualResponse).hasSize(expectedResponse.size());
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }




    private  String getPriceForRealmUrl(Realm realm) {
        if (realm.getFaction() == null) {
            return String.format("%s/%s-%s", API_REALMS, realm.getName(), "");
        }
        return String.format("%s/%s-%s", API_REALMS, realm.getName(), realm.getFaction());
    }
    private  String getAllPricesForRealmUrl(Realm realm) {
        if (realm.getFaction() == null) {
            return String.format("%s/%s-%s/all", API_REALMS, realm.getName(), "");
        }
        return String.format("%s/%s-%s/all", API_REALMS, realm.getName(), realm.getFaction());
    }
}