package com.example.g2gcalculator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TokenRequest {
    @JsonProperty("client_id")
    private String clientId;
    @JsonProperty("grant_type")
    private String grantType;
    @JsonProperty("scope")
    private String scope;
    @JsonProperty("token")
    private String token;
}