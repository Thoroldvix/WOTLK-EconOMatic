package com.thoroldvix.pricepal.server.error;


import com.thoroldvix.pricepal.common.error.NotFoundException;

public class ServerPriceNotFoundException extends NotFoundException {
    public ServerPriceNotFoundException(String s) {
        super(s);
    }
}
