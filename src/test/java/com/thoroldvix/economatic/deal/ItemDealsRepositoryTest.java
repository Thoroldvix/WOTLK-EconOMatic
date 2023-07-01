package com.thoroldvix.economatic.deal;

import com.thoroldvix.economatic.PostgreSqlContainerInitializer;
import com.thoroldvix.economatic.deal.repository.ItemDealProjection;
import com.thoroldvix.economatic.deal.repository.ItemDealsRepository;
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
public class ItemDealsRepositoryTest implements PostgreSqlContainerInitializer {
    public static final String ITEM_NAME = "Arcane Dust";
    private static final String SERVER_NAME = "everlook-alliance";
    private static final int ITEM_ID = 22445;
    private static final int MARKET_VALUE = 24762;
    private static final int MIN_BUYOUT = 9598;
    @Autowired
    private ItemDealsRepository itemDealsRepository;


    @Test
    public void testFindDealsForServer_withLimit_shouldReturnCorrectListSize() {
        List<ItemDealProjection> dealsForServer = itemDealsRepository.findDealsForServer(41003, 1, 0, 3);
        assertThat(dealsForServer).hasSize(3);
    }

    @Test
    public void testFindDealsForServer_withMinQuality_shouldReturnEmptyList() {
        List<ItemDealProjection> dealsForServer = itemDealsRepository.findDealsForServer(41003, 1, 5, 3);
        assertThat(dealsForServer).isEmpty();
    }

    @Test
    public void testFindDealsForServer_withMinQuantity_shouldReturnCorrectListSize() {
        List<ItemDealProjection> dealsForServer = itemDealsRepository.findDealsForServer(41003, 500, 0, 4);
        assertThat(dealsForServer).hasSize(2);
    }

    @Test
    public void testDealProjection_isCorrect() {
        ItemDealProjection dealForServer = itemDealsRepository.findDealsForServer(41003, 2848, 0, 1).get(0);


        assertSoftly(softly -> {
                    softly.assertThat(dealForServer.getItemId()).isEqualTo(ITEM_ID);
                    softly.assertThat(dealForServer.getUniqueServerName()).isEqualTo(SERVER_NAME);
                    softly.assertThat(dealForServer.getMarketValue()).isEqualTo(MARKET_VALUE);
                    softly.assertThat(dealForServer.getMinBuyout()).isEqualTo(MIN_BUYOUT);
                    softly.assertThat(dealForServer.getDealDiff()).isEqualTo(calculateDealDiff());
                    softly.assertThat(dealForServer.getDiscountPercentage()).isEqualTo(calculateDiscountPercentage());
                    softly.assertThat(dealForServer.getItemName()).isEqualTo(ITEM_NAME);
                    softly.assertAll();
                });
    }

    private int calculateDealDiff() {
        return MARKET_VALUE - MIN_BUYOUT;
    }

    private BigDecimal calculateDiscountPercentage() {
        int dealDiff = calculateDealDiff();
        return new BigDecimal((double) dealDiff / MARKET_VALUE)
                .setScale(6, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
    }
}