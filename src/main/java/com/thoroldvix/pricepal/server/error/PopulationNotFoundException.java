package com.thoroldvix.pricepal.server.error;

import com.vaadin.flow.router.NotFoundException;

public class PopulationNotFoundException extends NotFoundException {

    public PopulationNotFoundException(String s) {
        super(s);
    }
}
