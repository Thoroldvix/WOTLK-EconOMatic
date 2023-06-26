package com.thoroldvix.economatic.goldprice.server.service;

import com.thoroldvix.economatic.server.ServerServiceImpl;
import com.thoroldvix.economatic.goldprice.GoldPricesResponse;
import com.thoroldvix.economatic.goldprice.GoldPrice;
import com.thoroldvix.economatic.goldprice.GoldPriceRepository;
import com.thoroldvix.economatic.goldprice.GoldPriceMapper;
import com.thoroldvix.economatic.goldprice.GoldPriceService;
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
class GoldPriceServiceTest {

    private static List<GoldPricesResponse> priceResponses;
    private static List<GoldPrice> prices;
    private static FilterRequest filters;
    private final Pageable unpaged = Pageable.unpaged();
    @Mock
    private GoldPriceRepository goldPriceRepository;
    @Mock
    private ServerServiceImpl serverServiceImpl;
    @Mock
    private GoldPriceMapper goldPriceMapper;
    @InjectMocks
    private GoldPriceService goldPriceService;


}