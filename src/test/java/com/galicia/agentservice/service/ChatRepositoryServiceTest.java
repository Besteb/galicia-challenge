package com.galicia.agentservice.service;

import com.galicia.agentservice.model.Chat;
import com.galicia.agentservice.model.ChatRequest;
import com.galicia.agentservice.model.Message;
import com.galicia.agentservice.model.MessageSenderType;
import com.galicia.agentservice.respository.ChatRepository;
import com.galicia.agentservice.respository.MessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatRepositoryServiceTest {
    @Mock
    private ChatRepository chatRepository;

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private ChatHistoryService chatHistoryService;

    @Test
    void createChat_shouldSaveAndReturnNewChat() {
        Long userId = 100L;

        when(chatRepository.save(any(Chat.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Chat result = chatHistoryService.createChat(userId);

        ArgumentCaptor<Chat> captor = ArgumentCaptor.forClass(Chat.class);
        verify(chatRepository, times(1)).save(captor.capture());

        Chat saved = captor.getValue();
        assertThat(saved.getUserId()).isEqualTo(userId);

        assertThat(result).isSameAs(saved);
    }

    @Test
    void trackUserMessage_shouldBuildAndSaveUserMessage() {
        ChatRequest request = new ChatRequest();
        request.setChatId(1L);
        request.setPrompt("HOLA TEST");
        request.setContext("Contexto actual");

        chatHistoryService.trackUserMessage(request);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository).save(messageCaptor.capture());

        Message capturedMessage = messageCaptor.getValue();

        assertThat(capturedMessage.getChatId()).isEqualTo(request.getChatId());
        assertThat(capturedMessage.getPrompt()).isEqualTo(request.getPrompt());
        assertThat(capturedMessage.getImmediateContext()).isEqualTo(request.getContext());
        assertThat(capturedMessage.getMessageSenderType()).isEqualTo(MessageSenderType.USER);
    }

    @Test
    void trackAgentMessage_shouldBuildAndSaveUserMessage() {
        String agentResponseString = "RESPUESTA DEL AGENTE";
        ChatRequest request = new ChatRequest();
        request.setChatId(1L);
        request.setPrompt("HOLA TEST");
        request.setContext("Contexto actual");

        chatHistoryService.trackAgentMessage(request, agentResponseString);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository).save(messageCaptor.capture());

        Message capturedMessage = messageCaptor.getValue();

        assertThat(capturedMessage.getChatId()).isEqualTo(request.getChatId());
        assertThat(capturedMessage.getPrompt()).isEqualTo(request.getPrompt());
        assertThat(capturedMessage.getImmediateContext()).isEqualTo(request.getContext());
        assertThat(capturedMessage.getMessage()).isEqualTo(agentResponseString);
        assertThat(capturedMessage.getMessageSenderType()).isEqualTo(MessageSenderType.AGENT);
    }
}
