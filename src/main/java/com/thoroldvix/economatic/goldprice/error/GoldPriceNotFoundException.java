package com.thoroldvix.economatic.goldprice.error;


import com.thoroldvix.economatic.error.NotFoundException;

public class GoldPriceNotFoundException extends NotFoundException {
    public GoldPriceNotFoundException(String s) {
        super(s);
    }
}
