package com.thoroldvix.economatic.item;

public class ItemAlreadyExistsException extends IllegalArgumentException {
    public ItemAlreadyExistsException(String s) {
        super(s);
    }

    public ItemAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public ItemAlreadyExistsException(Throwable cause) {
        super(cause);
    }



    public ItemAlreadyExistsException() {
    }
}
