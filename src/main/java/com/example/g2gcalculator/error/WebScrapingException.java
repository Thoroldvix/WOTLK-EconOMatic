package com.example.g2gcalculator.error;

public class WebScrapingException extends RuntimeException {
    public WebScrapingException(String message) {
        super(message);
    }
}