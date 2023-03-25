package com.example.g2gcalculator.dto;

import lombok.Data;

@Data
public class TokenRequest {
    private String clientId;
    private String grantType;
    private String scope;
    private String token;
}