package com.example.g2gcalculator.service;

public interface AuthService {

    String getAccessToken();

    void refreshToken();
}