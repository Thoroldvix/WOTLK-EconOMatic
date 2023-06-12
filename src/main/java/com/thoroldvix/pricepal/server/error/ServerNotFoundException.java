package com.thoroldvix.pricepal.server.error;


import com.thoroldvix.pricepal.common.error.NotFoundException;

public class ServerNotFoundException extends NotFoundException {
    public ServerNotFoundException(String s) {
        super(s);
    }
}
