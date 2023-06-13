package com.thoroldvix.pricepal.goldprice;

public class GoldPriceParsingException extends RuntimeException{
    public GoldPriceParsingException() {
    }

    public GoldPriceParsingException(String message) {
        super(message);
    }

    public GoldPriceParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public GoldPriceParsingException(Throwable cause) {
        super(cause);
    }

    public GoldPriceParsingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
