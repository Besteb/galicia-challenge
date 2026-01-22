package com.galicia.agentservice.service;

import com.galicia.agentservice.client.llm.LlmProvider;
import com.galicia.agentservice.model.MenuOptions;
import com.galicia.agentservice.model.PromptResources;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LLMServiceTest {
    @Mock
    private ResourceLoader resourceLoader;

    @Mock
    private LlmProvider llmProvider;

    @Mock
    private Resource resource;

    @InjectMocks
    private LLMService llmService;

    @Test
    void createResponse_shoudlFormatAndCallProvider() {
        String prePromt = "Crea una tabla con %s";
        String arg = "Datos, datos, datos";
        String expectedPrompt = prePromt.formatted(arg);
        String expectedResponse = "Respuesta del agente";

        when(llmProvider.generateContent(expectedPrompt)).thenReturn(expectedResponse);

        String response = llmService.createResponse(prePromt, arg);

        assertThat(response).isEqualTo(expectedResponse);
        verify(llmProvider).generateContent(expectedPrompt);
    }

    @Test
    void createResponseFromResourcePrompt_shouldLoadFileAndCallProvider() throws IOException {
        PromptResources resourceEnum = PromptResources.CONSTRUCT_FROM_PROMPT;
        String fileContent = "El contenido del prompt es: %s";
        String arg = "Data, data, data";
        String expectedFormattedPrompt = fileContent.formatted(arg);
        String expectedResponse = "Respuesta del agente";

        when(resourceLoader.getResource("classpath:prompts/" + resourceEnum)).thenReturn(resource);
        when(resource.getInputStream()).thenReturn(new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8)));

        when(llmProvider.generateContent(expectedFormattedPrompt)).thenReturn(expectedResponse);

        String result = llmService.createResponseFromResourcePrompt(resourceEnum, arg);

        assertThat(result).isEqualTo(expectedResponse);
    }

    @Test
    void createResponseFromResourcePrompt_whenResourceFails_shouldReturnErrorPrompt() throws IOException {
        PromptResources resourceEnum = PromptResources.CONSTRUCT_FROM_PROMPT;
        String errorPromptText = "ESTRICTO: Respondé sólo en una frase corta y formal, que hubo un problema, pero sin explicar cuál es y que vuelva a intentarlo. Sin HTML.";
        String expectedErrorResponse = "Mensaje genérico de errores";

        when(resourceLoader.getResource(anyString())).thenReturn(resource);
        when(resource.getInputStream()).thenThrow(new IOException("File not found"));

        when(llmProvider.generateContent(errorPromptText)).thenReturn(expectedErrorResponse);

        String result = llmService.createResponseFromResourcePrompt(resourceEnum, "arg");

        assertThat(result).isEqualTo(expectedErrorResponse);
    }

    @Test
    void userPromptToMenuOptions_shouldParseEnumCorrectly() throws IOException {
        String userPrompt = "Quiero cotizaciones";
        String context = "Contexto previo";
        String menuOptionsString = Arrays.toString(MenuOptions.values());
        String fileContent = "Contexto: %s, Input: %s";
        String expectedPrompt = String.format(fileContent, context, menuOptionsString, userPrompt);
        String llmOutput = "COTIZACIONES";

        when(resourceLoader.getResource(anyString())).thenReturn(resource);
        when(resource.getInputStream()).thenReturn(new ByteArrayInputStream(fileContent.getBytes()));
        when(llmProvider.generateContent(expectedPrompt)).thenReturn(llmOutput);

        MenuOptions result = llmService.userPromptToMenuOptions(userPrompt, context);

        assertThat(result).isEqualTo(MenuOptions.COTIZACIONES);
    }

    @Test
    void userPromptToMenuOptions_whenLlmReturnsInvalidEnum_shouldThrowException() throws IOException {
        String llmOutput = "ALUCINACION";

        when(resourceLoader.getResource(anyString())).thenReturn(resource);
        when(resource.getInputStream()).thenReturn(new ByteArrayInputStream("Prompt %s %s %s".getBytes()));
        when(llmProvider.generateContent(anyString())).thenReturn(llmOutput);

        assertThatThrownBy(() -> llmService.userPromptToMenuOptions("input", "ctx"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}