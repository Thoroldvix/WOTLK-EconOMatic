package com.thoroldvix.economatic.population.error;


import com.thoroldvix.economatic.error.NotFoundException;

public class PopulationNotFoundException extends NotFoundException {
    public PopulationNotFoundException(String s) {
        super(s);
    }
}
