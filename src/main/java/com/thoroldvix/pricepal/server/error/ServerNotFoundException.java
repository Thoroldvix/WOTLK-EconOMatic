package com.thoroldvix.pricepal.server.error;

public class ServerNotFoundException extends RuntimeException {

    public ServerNotFoundException(String s) {
        super(s);
    }
}
