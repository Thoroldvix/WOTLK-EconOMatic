package com.thoroldvix.economatic.shared;

public interface StatsProjection {
    Number getMean();

    Number getMaxId();

    Number getMinId();

    Number getMedian();

    long getCount();


}
