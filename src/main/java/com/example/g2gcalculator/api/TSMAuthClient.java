package com.example.g2gcalculator.api;

import com.example.g2gcalculator.dto.TokenRequest;
import com.example.g2gcalculator.dto.TokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "authClient")
public interface TSMAuthClient {
    @PostMapping("/oauth2/token")
    TokenResponse getToken(TokenRequest tokenRequest);
}