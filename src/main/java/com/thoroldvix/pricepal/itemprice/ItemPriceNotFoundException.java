package com.thoroldvix.pricepal.itemprice;

import com.thoroldvix.pricepal.error.NotFoundException;

public class ItemPriceNotFoundException extends NotFoundException {
    public ItemPriceNotFoundException(String s) {
        super(s);
    }

    public ItemPriceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ItemPriceNotFoundException(Throwable cause) {
        super(cause);
    }

    public ItemPriceNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ItemPriceNotFoundException() {
    }
}
