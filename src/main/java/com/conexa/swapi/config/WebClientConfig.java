package com.conexa.swapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.*;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient swapiWebClient() {
        return WebClient.builder()
                .baseUrl("https://www.swapi.tech/api")
                .build();
    }
}

