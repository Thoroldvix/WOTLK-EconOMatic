package com.thoroldvix.economatic.item;

public class ItemDoesNotExistException extends IllegalArgumentException {
    public ItemDoesNotExistException() {
    }

    public ItemDoesNotExistException(String s) {
        super(s);
    }

    public ItemDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public ItemDoesNotExistException(Throwable cause) {
        super(cause);
    }
}
