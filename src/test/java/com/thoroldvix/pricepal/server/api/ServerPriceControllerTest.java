package com.thoroldvix.pricepal.server.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoroldvix.pricepal.server.service.ServerPriceService;
import org.junit.internal.requests.FilterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(ServerPriceController.class)
@ActiveProfiles("test")
public class ServerPriceControllerTest {
    public static final String API_PRICES = "/wow-classic/api/v1/prices";
    private static FilterRequest filters;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ServerPriceService serverPriceService;




}