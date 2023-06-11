package com.thoroldvix.pricepal.server.service;

import com.thoroldvix.pricepal.server.dto.ServerPriceResponse;
import com.thoroldvix.pricepal.server.entity.ServerPrice;
import com.thoroldvix.pricepal.server.repository.ServerPriceRepository;
import org.junit.internal.requests.FilterRequest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public
class ServerPriceServiceTest {

    private static List<ServerPriceResponse> priceResponses;
    private static List<ServerPrice> prices;
    private static FilterRequest filters;
    private final Pageable unpaged = Pageable.unpaged();
    @Mock
    private ServerPriceRepository serverPriceRepository;
    @Mock
    private ServerService serverService;
    @Mock
    private ServerPriceMapper serverPriceMapper;
    @InjectMocks
    private ServerPriceService serverPriceService;


}