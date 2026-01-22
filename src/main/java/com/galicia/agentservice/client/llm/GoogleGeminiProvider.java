package com.galicia.agentservice.client.llm;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GoogleGeminiProvider implements LlmProvider {

    private final Client genAiClient; // Injected from your Config class

    @Value("${spring.ai.google.genai.model}")
    private String GEMINI_MODEL;

    @Override
    public String generateContent(String prompt) {
        // The specific Google call happens here, isolated from the rest of the app
        GenerateContentResponse response = genAiClient.models.generateContent(
                GEMINI_MODEL,
                prompt,
                null
        );
        return response.text();
    }
}
