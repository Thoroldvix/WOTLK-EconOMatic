package com.thoroldvix.pricepal.population;

import com.fasterxml.jackson.core.JsonProcessingException;

public class PopulationParsingException extends RuntimeException{
    public PopulationParsingException() {
    }

    public PopulationParsingException(String message) {
        super(message);
    }

    public PopulationParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public PopulationParsingException(Throwable cause) {
        super(cause);
    }

    public PopulationParsingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
