package com.galicia.agentservice.config;

import com.google.genai.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class GoogleAIConfig {

    @Value("${spring.ai.google.api-key}")
    private String API_KEY;

    @Bean
    public Client googleAIClient() {
        if (System.getenv("GOOGLE_API_KEY") == null) {
            System.setProperty("GOOGLE_API_KEY", API_KEY);
        }
        return Client.builder().apiKey(API_KEY).build();
    }
}
