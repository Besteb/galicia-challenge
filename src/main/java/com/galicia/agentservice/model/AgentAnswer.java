package com.galicia.agentservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class AgentAnswer {
    Long chatId;
    String message;
}
