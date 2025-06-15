package com.example.flightDataManagementService.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient crazySupplierWebClient() {
        return WebClient.builder().baseUrl("https://api.crazy-supplier.com").build();
    }

}
