package com.thoroldvix.economatic.item.error;

public class ItemDoesNotExistException extends IllegalArgumentException {
    public ItemDoesNotExistException(String s) {
        super(s);
    }
}
