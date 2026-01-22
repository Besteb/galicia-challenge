package com.galicia.agentservice.service;

import com.galicia.agentservice.client.llm.LlmProvider;
import com.galicia.agentservice.model.MenuOptions;
import com.galicia.agentservice.model.PromptResources;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class LLMService {
    private final ResourceLoader resourceLoader;
    private final LlmProvider llmProvider;


    public String createResponse(String prompt, Object... object) {
        prompt = String.format(prompt, object);

        return llmProvider.generateContent(prompt);
    }

    public String createResponseFromResourcePrompt(PromptResources resourcePromptName, Object... object) {
        try {
            String prePrompt = String.format("classpath:prompts/%s", resourcePromptName);
            Resource resource = resourceLoader.getResource(prePrompt);
            String prompt = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            return createResponse(prompt, object);
        } catch (Exception e) {
            return createErrorPrompt();
        }
    }

    public MenuOptions userPromptToMenuOptions(String userPrompt, String context) {
        String menuOptionValues = Arrays.toString(MenuOptions.values());
        String newValue = createResponseFromResourcePrompt(PromptResources.CONSTRUCT_FROM_PROMPT, context, menuOptionValues, userPrompt);
        return MenuOptions.valueOf(newValue.trim().replace("\n", ""));
    }

    public String createErrorPrompt() {
        String EXCEPTION_PROMPT = "ESTRICTO: Respondé sólo en una frase corta y formal, que hubo un problema, pero sin explicar cuál es y que vuelva a intentarlo. Sin HTML.";
        return createResponse(EXCEPTION_PROMPT);
    }
}
