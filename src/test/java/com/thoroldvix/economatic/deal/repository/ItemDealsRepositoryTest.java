package com.thoroldvix.economatic.deal.repository;

import com.thoroldvix.economatic.PostgresqlContainerInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Sql(scripts = "/add-item-price-data.sql")
public class ItemDealsRepositoryTest implements PostgresqlContainerInitializer {
    public static final String ITEM_NAME = "Arcane Dust";
    private static final String SERVER_NAME = "everlook-alliance";
    private static final int ITEM_ID = 22445;
    private static final int MARKET_VALUE = 24762;
    private static final int MIN_BUYOUT = 9598;
    @Autowired
    private ItemDealsRepository itemDealsRepository;


    @Test
    void findDealsForServer_returnsCorrectDealsSize_whenLimitIsSet() {
        List<ItemDealProjection> dealsForServer = itemDealsRepository.findDealsForServer(41003, 1, 0, 3);
        assertThat(dealsForServer).hasSize(3);
    }

    @Test
    void findDealsForServer_returnsEmptyList_whenMinQualityIsNotInDataset() {
        List<ItemDealProjection> dealsForServer = itemDealsRepository.findDealsForServer(41003, 1, 5, 4);
        assertThat(dealsForServer).isEmpty();
    }

    @Test
   void findDealsForServer_returnsCorrectDealsSize_WhenFilteredByMinQuality() {
        List<ItemDealProjection> dealsForServer = itemDealsRepository.findDealsForServer(41003, 1, 0, 4);
        assertThat(dealsForServer).hasSize(4);
    }

    @Test
     void findDealsForServer_returnsCorrectDealsSize_whenFilteredByMinQuantity() {
        List<ItemDealProjection> dealsForServer = itemDealsRepository.findDealsForServer(41003, 500, 0, 4);
        assertThat(dealsForServer).hasSize(2);
    }

    @Test
    void findDealsForServer_returnsCorrectFields_whenCalledWithSpecificParameters() {
        ItemDealProjection dealForServer = itemDealsRepository.findDealsForServer(41003, 2848, 0, 1).get(0);

        assertSoftly(softly -> {
            softly.assertThat(dealForServer.getItemId()).isEqualTo(ITEM_ID);
            softly.assertThat(dealForServer.getUniqueServerName()).isEqualTo(SERVER_NAME);
            softly.assertThat(dealForServer.getMarketValue()).isEqualTo(MARKET_VALUE);
            softly.assertThat(dealForServer.getMinBuyout()).isEqualTo(MIN_BUYOUT);
            softly.assertThat(dealForServer.getDealDiff()).isEqualTo(calculateDealDiff());
            softly.assertThat(dealForServer.getDiscountPercentage().setScale(2, RoundingMode.HALF_UP)).isEqualTo(calculateDiscountPercentage());
            softly.assertThat(dealForServer.getItemName()).isEqualTo(ITEM_NAME);
            softly.assertAll();
        });
    }

    private int calculateDealDiff() {
        return MARKET_VALUE - MIN_BUYOUT;
    }

    private BigDecimal calculateDiscountPercentage() {
        double dealDiff = calculateDealDiff();
        double dealPercentage = dealDiff / MARKET_VALUE;
        return BigDecimal.valueOf(dealPercentage)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }
}