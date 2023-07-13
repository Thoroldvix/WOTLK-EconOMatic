package com.thoroldvix.economatic.deal;


import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.List;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public abstract class BaseItemDealTest {
    protected static final int MINIMUM_ITEM_QUANTITY = 3;
    protected static final int MINIMUM_ITEM_QUALITY = 0;
    protected static final int ITEM_LIMIT = 5;
    protected static final int SERVER_ID = 41003;
    protected static final String SERVER_NAME = "everlook-alliance";

    // Constants for Item 1
    protected static final int ITEM1_ID = 22449;
    protected static final int ITEM1_DEAL_DIFFERENCE = 3452;
    protected static final int ITEM1_MINIMUM_BUYOUT = 74989;
    protected static final int ITEM1_MARKET_VALUE = 78441;
    protected static final BigDecimal ITEM1_DISCOUNT_PERCENTAGE = new BigDecimal("4.40");
    protected static final String ITEM1_NAME = "Large Prismatic Shard";

    // Constants for Item 2
    protected static final int ITEM2_ID = 22445;
    protected static final int ITEM2_DEAL_DIFFERENCE = 15164;
    protected static final int ITEM2_MINIMUM_BUYOUT = 9598;
    protected static final int ITEM2_MARKET_VALUE = 24762;
    protected static final BigDecimal ITEM2_DISCOUNT_PERCENTAGE = new BigDecimal("61.24");
    protected static final String ITEM2_NAME = "Arcane Dust";


    protected static ItemDealProjection getDealProjection() {
        return new ItemDealProjection() {
            @Override
            public int getItemId() {
                return ITEM1_ID;
            }

            @Override
            public String getUniqueServerName() {
                return SERVER_NAME;
            }

            @Override
            public long getMarketValue() {
                return ITEM1_MARKET_VALUE;
            }

            @Override
            public long getMinBuyout() {
                return ITEM1_MINIMUM_BUYOUT;
            }

            @Override
            public long getDealDiff() {
                return ITEM1_DEAL_DIFFERENCE;
            }

            @Override
            public BigDecimal getDiscountPercentage() {
                return ITEM1_DISCOUNT_PERCENTAGE;
            }

            @Override
            public String getItemName() {
                return ITEM1_NAME;
            }
        };
    }

    protected static ItemDealResponse getItemDealResponse() {
        return ItemDealResponse.builder()
                .itemId(ITEM1_ID)
                .server(SERVER_NAME)
                .dealDiff(ITEM1_DEAL_DIFFERENCE)
                .itemName(ITEM1_NAME)
                .discountPercentage(ITEM1_DISCOUNT_PERCENTAGE)
                .marketValue(ITEM1_MARKET_VALUE)
                .minBuyout(ITEM1_MINIMUM_BUYOUT)
                .build();
    }

    protected static List<ItemDealResponse> getItemDealResponseList() {
        return List.of(new ItemDealResponse(ITEM1_ID, ITEM1_MARKET_VALUE, ITEM1_MINIMUM_BUYOUT
                , ITEM1_DEAL_DIFFERENCE, SERVER_NAME, ITEM1_DISCOUNT_PERCENTAGE, ITEM1_NAME), new ItemDealResponse(ITEM2_ID, ITEM2_MARKET_VALUE, ITEM2_MINIMUM_BUYOUT
                , ITEM2_DEAL_DIFFERENCE, SERVER_NAME, ITEM2_DISCOUNT_PERCENTAGE, ITEM2_NAME));
    }

    protected static ItemDealsList getItemDealsList() {
        return new ItemDealsList(getItemDealResponseList());
    }
}
