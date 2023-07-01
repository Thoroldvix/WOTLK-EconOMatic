package com.thoroldvix.economatic.item.error;

public class ItemAlreadyExistsException extends IllegalArgumentException {
    public ItemAlreadyExistsException(String s) {
        super(s);
    }
}
