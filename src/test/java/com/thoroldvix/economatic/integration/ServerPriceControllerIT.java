package com.thoroldvix.economatic.integration;

import com.thoroldvix.economatic.PostgreSqlContainerInitializer;
import com.thoroldvix.economatic.goldprice.GoldPriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;


@SpringBootTest
@AutoConfigureMockMvc
public class ServerPriceControllerIT implements PostgreSqlContainerInitializer {

    @Autowired
    private GoldPriceRepository goldPriceRepository;

    @Autowired
    private MockMvc mvc;



}
