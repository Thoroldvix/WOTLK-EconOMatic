package com.thoroldvix.pricepal.server;


import com.thoroldvix.pricepal.error.NotFoundException;

public class ServerNotFoundException extends NotFoundException {
    public ServerNotFoundException(String s) {
        super(s);
    }
}
