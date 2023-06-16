package com.thoroldvix.pricepal.goldprice.server.service;

import com.thoroldvix.pricepal.server.ServerMapper;
import com.thoroldvix.pricepal.server.ServerRepository;
import com.thoroldvix.pricepal.server.ServerServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ServerServiceImplTest {
    @Mock
    private ServerRepository serverRepository;
    @Mock
    private ServerMapper serverMapper;
    @InjectMocks
    private ServerServiceImpl serverServiceImpl;



}