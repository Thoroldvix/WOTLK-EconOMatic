package com.thoroldvix.economatic.goldprice;


import com.thoroldvix.economatic.error.NotFoundException;

class GoldPriceNotFoundException extends NotFoundException {
    public GoldPriceNotFoundException(String s) {
        super(s);
    }
}
