package com.galicia.agentservice.service;

import com.galicia.agentservice.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgentService {

    private final LLMService llmService;
    private final ChatHistoryService chatHistoryService;
    private final CurrencyService currencyService;


    public AgentAnswer preProcessChat(ChatRequest chatRequest) {
        if (chatRequest.getChatId() == null) {
            return startChat(chatRequest.getUserId());
        }

        MenuOptions menuOptions = chatRequest.getMenuOptions();

        if (menuOptions == null) {
            menuOptions = llmService.userPromptToMenuOptions(chatRequest.getPrompt(), chatRequest.getContext());
        }

        AgentAnswer agentAnswer = chat(chatRequest.getChatId(), menuOptions);

        chatHistoryService.trackUserMessage(chatRequest);
        chatHistoryService.trackAgentMessage(chatRequest, agentAnswer.getMessage());

        return agentAnswer;
    }

    private AgentAnswer startChat(Long userId) {
        Chat newChat = chatHistoryService.createChat(userId);

        return chat(newChat.getId(), MenuOptions.WELCOME);
    }

    public AgentAnswer chat(Long chatId, MenuOptions menuOptions) {
        if (menuOptions == MenuOptions.WELCOME) {
            return toAgentAnswer(chatId, llmService.createResponseFromResourcePrompt(PromptResources.WELCOME, Arrays.toString(MenuOptions.values())));
        }

        return toAgentAnswer(chatId, processCurrencyRateRequest(menuOptions));
    }

    private AgentAnswer toAgentAnswer(Long chatId, String message) {
        return new AgentAnswer(chatId, message);
    }

    private String processCurrencyRateRequest(MenuOptions menuOptions) {
        List<CurrencyResponse> responseList = currencyService.getCurrencyInfoForCurrency(menuOptions);

        if (responseList.isEmpty()) {
            return llmService.createErrorPrompt();
        }

        return llmService.createResponseFromResourcePrompt(PromptResources.COIN_DATA_LIST, responseList);
    }
}

