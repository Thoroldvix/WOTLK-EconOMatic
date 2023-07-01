package com.thoroldvix.economatic.itemprice.error;

import com.thoroldvix.economatic.error.NotFoundException;

public class ItemPriceNotFoundException extends NotFoundException {
    public ItemPriceNotFoundException(String s) {
        super(s);
    }
}
