package com.thoroldvix.pricepal.server.service;

import com.thoroldvix.pricepal.server.repository.ServerRepository;
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