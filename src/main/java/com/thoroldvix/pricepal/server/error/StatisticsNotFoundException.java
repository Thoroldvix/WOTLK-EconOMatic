package com.thoroldvix.pricepal.server.error;


import com.thoroldvix.pricepal.common.error.NotFoundException;

public class StatisticsNotFoundException extends NotFoundException {

    public StatisticsNotFoundException(String s) {
        super(s);
    }
}
