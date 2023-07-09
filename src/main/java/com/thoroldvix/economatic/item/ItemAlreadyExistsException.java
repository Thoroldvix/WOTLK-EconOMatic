package com.thoroldvix.economatic.item;

public class ItemAlreadyExistsException extends IllegalArgumentException {
    public ItemAlreadyExistsException(String s) {
        super(s);
    }
}
