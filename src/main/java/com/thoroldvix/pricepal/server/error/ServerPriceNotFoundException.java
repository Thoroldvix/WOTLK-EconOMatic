package com.thoroldvix.pricepal.server.error;

import com.vaadin.flow.router.NotFoundException;

public class ServerPriceNotFoundException extends NotFoundException {
    public ServerPriceNotFoundException(String s) {
        super(s);
    }
}
