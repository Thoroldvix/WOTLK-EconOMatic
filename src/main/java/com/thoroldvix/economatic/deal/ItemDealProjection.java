package com.thoroldvix.economatic.deal;

import java.math.BigDecimal;
interface ItemDealProjection {
    int getItemId();
    String getUniqueServerName();
    long getMarketValue();
    long getMinBuyout();
    long getDealDiff();
    BigDecimal getDiscountPercentage();
    String getItemName();
}
