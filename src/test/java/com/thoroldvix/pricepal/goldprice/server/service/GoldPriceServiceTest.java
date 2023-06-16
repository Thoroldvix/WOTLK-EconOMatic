package com.thoroldvix.pricepal.goldprice.server.service;

import com.thoroldvix.pricepal.server.ServerServiceImpl;
import com.thoroldvix.pricepal.goldprice.GoldPriceResponse;
import com.thoroldvix.pricepal.goldprice.GoldPrice;
import com.thoroldvix.pricepal.goldprice.GoldPriceRepository;
import com.thoroldvix.pricepal.goldprice.GoldPriceMapper;
import com.thoroldvix.pricepal.goldprice.GoldPriceService;
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

    private static List<GoldPriceResponse> priceResponses;
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