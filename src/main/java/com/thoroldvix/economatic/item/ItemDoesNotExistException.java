package com.thoroldvix.economatic.item;

public class ItemDoesNotExistException extends IllegalArgumentException {
    public ItemDoesNotExistException(String s) {
        super(s);
    }
}
