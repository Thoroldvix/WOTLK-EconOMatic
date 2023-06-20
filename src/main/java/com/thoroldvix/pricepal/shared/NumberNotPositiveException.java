package com.thoroldvix.pricepal.shared;

public class NumberNotPositiveException extends RuntimeException{
    public NumberNotPositiveException() {
    }

    public NumberNotPositiveException(String message) {
        super(message);
    }

    public NumberNotPositiveException(String message, Throwable cause) {
        super(message, cause);
    }

    public NumberNotPositiveException(Throwable cause) {
        super(cause);
    }

    public NumberNotPositiveException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
