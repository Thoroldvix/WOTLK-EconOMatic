package com.thoroldvix.pricepal.goldprice.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoroldvix.pricepal.server.ServerServiceImpl;
import com.thoroldvix.pricepal.goldprice.G2GPriceClient;
import com.thoroldvix.pricepal.goldprice.GoldPriceUpdateService;
import com.thoroldvix.pricepal.goldprice.GoldPriceService;
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