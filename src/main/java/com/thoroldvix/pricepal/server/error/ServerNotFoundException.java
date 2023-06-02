package com.thoroldvix.pricepal.server.error;

import com.vaadin.flow.router.NotFoundException;

public class ServerNotFoundException extends NotFoundException {

    public ServerNotFoundException(String s) {
        super(s);
    }
}
