package com.thoroldvix.economatic.item;

import com.thoroldvix.economatic.error.NotFoundException;

public class ItemDoesNotExistException extends NotFoundException {

    public ItemDoesNotExistException(String s) {
        super(s);
    }
}
