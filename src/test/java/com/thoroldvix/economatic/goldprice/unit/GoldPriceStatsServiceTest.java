package com.thoroldvix.economatic.goldprice.unit;

import com.thoroldvix.economatic.goldprice.GoldPriceMapper;
import com.thoroldvix.economatic.goldprice.GoldPriceStatMapper;
import com.thoroldvix.economatic.goldprice.GoldPriceStatRepository;
import com.thoroldvix.economatic.goldprice.GoldPriceStatsService;
import com.thoroldvix.economatic.server.ServerService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class GoldPriceStatsServiceTest {

    @Mock
    private ServerService serverService;

    @Mock
    private GoldPriceStatRepository goldPriceStatRepository;

    @Mock
    private GoldPriceStatMapper goldPriceStatMapper;

    @Mock
    private GoldPriceMapper goldPriceMapper;

    @InjectMocks
    private GoldPriceStatsService goldPriceStatsService;


    @Test
    void getForServer_returnsCorrectGoldPriceStatResponse() {

    }

}