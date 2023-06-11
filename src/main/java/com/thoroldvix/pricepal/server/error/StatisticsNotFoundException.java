package com.thoroldvix.pricepal.server.error;

import com.vaadin.flow.router.NotFoundException;

public class StatisticsNotFoundException extends NotFoundException {

    public StatisticsNotFoundException(String s) {
        super(s);
    }
}
