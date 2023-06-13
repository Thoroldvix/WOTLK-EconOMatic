package com.thoroldvix.pricepal.goldprice.server.service;

import com.thoroldvix.pricepal.server.ServerMapper;
import com.thoroldvix.pricepal.server.ServerRepository;
import com.thoroldvix.pricepal.server.ServerService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ServerServiceTest {
    @Mock
    private ServerRepository serverRepository;
    @Mock
    private ServerMapper serverMapper;
    @InjectMocks
    private ServerService serverService;



}