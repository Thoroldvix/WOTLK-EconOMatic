package com.thoroldvix.economatic.itemprice;

import com.thoroldvix.economatic.error.NotFoundException;

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
