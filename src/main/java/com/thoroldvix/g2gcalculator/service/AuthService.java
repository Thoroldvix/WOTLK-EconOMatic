package com.thoroldvix.g2gcalculator.service;

public interface AuthService {

    String getAccessToken();

    void refreshToken();
}