package com.thoroldvix.pricepal.goldprice;


import com.thoroldvix.pricepal.error.NotFoundException;

public class GoldPriceNotFoundException extends NotFoundException {
    public GoldPriceNotFoundException(String s) {
        super(s);
    }
}
