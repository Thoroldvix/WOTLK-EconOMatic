package com.thoroldvix.pricepal.server.error;


import com.thoroldvix.pricepal.common.error.NotFoundException;

public class PopulationNotFoundException extends NotFoundException {

    public PopulationNotFoundException(String s) {
        super(s);
    }
}
