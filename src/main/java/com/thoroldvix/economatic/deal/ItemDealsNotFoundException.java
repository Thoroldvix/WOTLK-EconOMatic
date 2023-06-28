package com.thoroldvix.economatic.deal;

import com.thoroldvix.economatic.error.NotFoundException;

public class ItemDealsNotFoundException extends NotFoundException {
    public ItemDealsNotFoundException(String s) {
        super(s);
    }

    public ItemDealsNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ItemDealsNotFoundException(Throwable cause) {
        super(cause);
    }

    public ItemDealsNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ItemDealsNotFoundException() {
    }
}
