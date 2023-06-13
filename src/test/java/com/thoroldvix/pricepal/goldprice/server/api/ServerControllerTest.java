package com.thoroldvix.pricepal.goldprice.server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoroldvix.pricepal.server.ServerController;
import com.thoroldvix.pricepal.server.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

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



}