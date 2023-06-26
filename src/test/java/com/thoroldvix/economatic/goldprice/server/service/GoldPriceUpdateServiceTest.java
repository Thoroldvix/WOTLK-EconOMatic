package com.thoroldvix.economatic.goldprice.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoroldvix.economatic.server.ServerServiceImpl;
import com.thoroldvix.economatic.goldprice.G2GPriceClient;
import com.thoroldvix.economatic.goldprice.GoldPriceUpdateService;
import com.thoroldvix.economatic.goldprice.GoldPriceService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GoldPriceUpdateServiceTest {

    @Mock
    G2GPriceClient g2GPriceClient;

    @Mock
    ServerServiceImpl serverServiceImpl;

    @Mock
    GoldPriceService goldPriceServiceImpl;

    private ObjectMapper mapper;

    @InjectMocks
    GoldPriceUpdateService goldPriceUpdateService;





}