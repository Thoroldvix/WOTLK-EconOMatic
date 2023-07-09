package com.thoroldvix.economatic.server;


import com.thoroldvix.economatic.error.NotFoundException;

public class ServerNotFoundException extends NotFoundException {
    public ServerNotFoundException(String s) {
        super(s);
    }
}
