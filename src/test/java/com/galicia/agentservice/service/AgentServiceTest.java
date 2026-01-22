package com.galicia.agentservice.service;

import com.galicia.agentservice.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgentServiceTest {
    @Mock
    private LLMService llmService;
    @Mock
    private ChatHistoryService chatHistoryService;
    @Mock
    private CurrencyService currencyService;

    @InjectMocks
    private AgentService agentService;

    @Test
    void preProcessChat_shouldCreateNewChat_whenChatIdEqualsNull() {
        Long userId = 100L;
        Long newChatId = 55L;
        String agentMessage = "Mensaje del agente";

        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setChatId(null);
        chatRequest.setUserId(userId);

        Chat newChat = new Chat();
        newChat.setId(newChatId);

        when(chatHistoryService.createChat(userId)).thenReturn(newChat);
        when(llmService.createResponseFromResourcePrompt(eq(PromptResources.WELCOME), anyString()))
                .thenReturn(agentMessage);

        AgentAnswer agentAnswer = agentService.preProcessChat(chatRequest);

        assertThat(agentAnswer.getChatId()).isEqualTo(newChatId);
        assertThat(agentAnswer.getMessage()).isEqualTo(agentMessage);

        verify(chatHistoryService, never()).trackUserMessage(chatRequest);
        verify(chatHistoryService, never()).trackAgentMessage(chatRequest, agentMessage);
    }

    @Test
    void preProcessChat_shouldUseLLMToClassify_whenOptionIsNull() {
        Long chatId = 1L;
        String userPrompt = "Cotización del dolar oficial";
        String context = "Contexto";
        String agentResponse = "La cotización oficial actual del dolar es $1500";

        ChatRequest request = new ChatRequest();
        request.setChatId(chatId);
        request.setPrompt(userPrompt);
        request.setContext(context);
        request.setMenuOptions(null);

        List<CurrencyResponse> currencyData = List.of(new CurrencyResponse());

        when(llmService.userPromptToMenuOptions(userPrompt, context)).thenReturn(MenuOptions.OFICIAL);
        when(currencyService.getCurrencyInfoForCurrency(MenuOptions.OFICIAL)).thenReturn(currencyData);
        when(llmService.createResponseFromResourcePrompt(eq(PromptResources.COIN_DATA_LIST), eq(currencyData)))
                .thenReturn(agentResponse);

        AgentAnswer result = agentService.preProcessChat(request);

        verify(llmService).userPromptToMenuOptions(userPrompt, context);
        verify(currencyService).getCurrencyInfoForCurrency(MenuOptions.OFICIAL);

        assertThat(result.getMessage()).isEqualTo(agentResponse);
        assertThat(result.getChatId()).isEqualTo(chatId);
    }
    @Test
    void preProcessChat_shouldSkipLLM_whenOptionIsProvided() {
        Long chatId = 1L;
        ChatRequest request = new ChatRequest();
        request.setChatId(chatId);
        request.setMenuOptions(MenuOptions.BRL);

        List<CurrencyResponse> currencyData = List.of(new CurrencyResponse());
        String agentResponse = "Cotización del Real Brasileño $500";

        when(currencyService.getCurrencyInfoForCurrency(MenuOptions.BRL)).thenReturn(currencyData);
        when(llmService.createResponseFromResourcePrompt(any(), any())).thenReturn(agentResponse);

        agentService.preProcessChat(request);

        verify(llmService, never()).userPromptToMenuOptions(any(), any());
        verify(currencyService).getCurrencyInfoForCurrency(MenuOptions.BRL);
    }

    @Test
    void chat_shouldReturnErrorPrompt_whenCurrencyListIsEmpty() {
        ChatRequest request = new ChatRequest();
        request.setChatId(1L);
        request.setMenuOptions(MenuOptions.DOLARES);

        String errorMsg = "No hay datos";

        when(currencyService.getCurrencyInfoForCurrency(MenuOptions.DOLARES)).thenReturn(Collections.emptyList());
        when(llmService.createErrorPrompt()).thenReturn(errorMsg);

        AgentAnswer result = agentService.preProcessChat(request);

        assertThat(result.getMessage()).isEqualTo(errorMsg);
        verify(llmService).createErrorPrompt();
    }
}