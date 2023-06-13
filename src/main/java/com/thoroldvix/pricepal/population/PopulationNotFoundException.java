package com.thoroldvix.pricepal.population;


import com.thoroldvix.pricepal.error.NotFoundException;

public class PopulationNotFoundException extends NotFoundException {
    public PopulationNotFoundException(String s) {
        super(s);
    }
}
