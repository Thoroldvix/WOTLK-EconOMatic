package com.thoroldvix.pricepal.server.error;

public class ServerPriceNotFoundException extends RuntimeException {
    public ServerPriceNotFoundException(String s) {
        super(s);
    }
}
