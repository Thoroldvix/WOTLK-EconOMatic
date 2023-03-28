package com.thoroldvix.g2gcalculator.config;

import com.thoroldvix.g2gcalculator.service.AuthService;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients
@RequiredArgsConstructor
public class FeignConfig {

    private final AuthService tsmAuthService;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> requestTemplate.header("Authorization", "Bearer " + tsmAuthService.getAccessToken());
    }
}