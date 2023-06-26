package com.thoroldvix.economatic.population;


import com.thoroldvix.economatic.error.NotFoundException;

public class PopulationNotFoundException extends NotFoundException {
    public PopulationNotFoundException(String s) {
        super(s);
    }
}
