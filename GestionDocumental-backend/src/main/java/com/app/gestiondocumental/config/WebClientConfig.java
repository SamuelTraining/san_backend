package com.app.gestiondocumental.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient iaWebClient() {
        // Configura el "teléfono" para que siempre llame a tu API de Python
        // que está corriendo en el puerto 8000
        return WebClient.builder()
                .baseUrl("http://127.0.0.1:8000")
                .build();
    }
}