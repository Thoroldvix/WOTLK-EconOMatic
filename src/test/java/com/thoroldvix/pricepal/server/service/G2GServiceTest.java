package com.thoroldvix.pricepal.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoroldvix.pricepal.server.api.G2GPriceClient;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class G2GServiceTest {

    @Mock
    G2GPriceClient g2GPriceClient;

    @Mock
    ServerService serverServiceImpl;

    @Mock
    ServerPriceService serverPriceServiceImpl;

    private ObjectMapper mapper;

    @InjectMocks
    G2GService g2GService;





}