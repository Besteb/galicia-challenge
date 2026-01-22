package com.galicia.agentservice.model;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class ChatRequest {
    private Long chatId;
    private Long userId;
    private String userName;
    private String prompt;
    private String context;
    private MenuOptions menuOptions;
}
