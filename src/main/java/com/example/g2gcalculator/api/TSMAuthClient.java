package com.example.g2gcalculator.api;

import com.example.g2gcalculator.dto.TokenRequest;
import com.example.g2gcalculator.dto.TokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "authClient", url= "https://auth.tradeskillmaster.com/oauth2/token")
public interface TSMAuthClient {
    @PostMapping(consumes = "application/json", produces = "application/json")
    TokenResponse getToken(TokenRequest tokenRequest);
}