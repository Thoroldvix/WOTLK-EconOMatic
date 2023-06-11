package com.thoroldvix.pricepal.server.error;


import com.thoroldvix.pricepal.common.error.NotFoundException;

public class G2GPriceNotFoundException extends NotFoundException {
    public G2GPriceNotFoundException(String s) {
        super(s);
    }
}