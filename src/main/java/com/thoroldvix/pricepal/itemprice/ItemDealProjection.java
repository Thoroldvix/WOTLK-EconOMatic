package com.thoroldvix.pricepal.itemprice;

import java.math.BigDecimal;

public interface ItemDealProjection {
    int getItemId();
    String getUniqueServerName();
    long getMarketValue();
    long getMinBuyout();
    long getDealDiff();
    BigDecimal getDiscountPercentage();
    String getItemName();


}
