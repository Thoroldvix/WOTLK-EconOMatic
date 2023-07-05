package com.thoroldvix.economatic.deal.repository;

import com.thoroldvix.economatic.PostgresqlContainerInitializer;
import com.thoroldvix.economatic.deal.BaseItemDealTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.math.RoundingMode;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/sql/deals/deals-repository-data.sql")
class ItemDealsRepositoryTest extends BaseItemDealTest implements PostgresqlContainerInitializer {

    @Autowired
    private ItemDealsRepository itemDealsRepository;


    @Test
    void findDealsForServer_returnsCorrectDealsSize_whenLimitIsSet() {
        List<ItemDealProjection> dealsForServer = itemDealsRepository.findDealsForServer(SERVER_ID, 1, 0, 3);
        assertThat(dealsForServer).hasSize(3);
    }

    @Test
    void findDealsForServer_returnsEmptyList_whenMinQualityIsNotInDataset() {
        List<ItemDealProjection> dealsForServer = itemDealsRepository.findDealsForServer(SERVER_ID, 1, 5, 4);
        assertThat(dealsForServer).isEmpty();
    }

    @Test
    void findDealsForServer_returnsCorrectDealsSize_WhenFilteredByMinQuality() {
        List<ItemDealProjection> dealsForServer = itemDealsRepository.findDealsForServer(SERVER_ID, 1, 0, 4);
        assertThat(dealsForServer).hasSize(4);
    }

    @Test
    void findDealsForServer_returnsCorrectDealsSize_whenFilteredByMinQuantity() {
        List<ItemDealProjection> dealsForServer = itemDealsRepository.findDealsForServer(SERVER_ID, 500, 0, 4);
        assertThat(dealsForServer).hasSize(2);
    }

    @Test
    void findDealsForServer_returnsCorrectFields_whenCalledWithSpecificParameters() {
        ItemDealProjection dealForServer = itemDealsRepository.findDealsForServer(SERVER_ID, 1, 0, 1).get(0);

        assertSoftly(softly -> {
            softly.assertThat(dealForServer.getItemId()).isEqualTo(ITEM2_ID);
            softly.assertThat(dealForServer.getUniqueServerName()).isEqualTo(SERVER_NAME);
            softly.assertThat(dealForServer.getMarketValue()).isEqualTo(ITEM2_MARKET_VALUE);
            softly.assertThat(dealForServer.getMinBuyout()).isEqualTo(ITEM2_MINIMUM_BUYOUT);
            softly.assertThat(dealForServer.getDealDiff()).isEqualTo(ITEM2_DEAL_DIFFERENCE);
            softly.assertThat(dealForServer.getDiscountPercentage().setScale(2, RoundingMode.HALF_UP)).isEqualTo(ITEM2_DISCOUNT_PERCENTAGE);
            softly.assertThat(dealForServer.getItemName()).isEqualTo(ITEM2_NAME);
            softly.assertAll();
        });
    }


}