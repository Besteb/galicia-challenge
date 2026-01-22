package com.galicia.agentservice.service;

import com.galicia.agentservice.model.Chat;
import com.galicia.agentservice.model.ChatRequest;
import com.galicia.agentservice.model.Message;
import com.galicia.agentservice.model.MessageSenderType;
import com.galicia.agentservice.respository.ChatRepository;
import com.galicia.agentservice.respository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatHistoryService {
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;

    public Chat createChat(Long userId) {
        return chatRepository.save(Chat.builder()
                .userId(userId)
                .build());
    }

    public void trackUserMessage(ChatRequest chatRequest) {
        if (chatRequest.getPrompt() == null && chatRequest.getMenuOptions() != null) {
            chatRequest.setPrompt(chatRequest.getMenuOptions().toString());
        }
        messageRepository.save(Message.builder()
                .chatId(chatRequest.getChatId())
                .prompt(chatRequest.getPrompt())
                .immediateContext(chatRequest.getContext())
                .messageSenderType(MessageSenderType.USER)
                .build());
    }

    public void trackAgentMessage(ChatRequest chatRequest, String agentAnswer) {
        messageRepository.save(Message.builder()
                .chatId(chatRequest.getChatId())
                .prompt(chatRequest.getPrompt())
                .message(agentAnswer)
                .immediateContext(chatRequest.getContext())
                .messageSenderType(MessageSenderType.AGENT)
                .build());
    }
}
