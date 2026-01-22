package com.galicia.agentservice.controller;

import com.galicia.agentservice.model.AgentAnswer;
import com.galicia.agentservice.model.ChatRequest;
import com.galicia.agentservice.service.AgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agent;

    @PostMapping(path = "/chat")
    public AgentAnswer chat(@RequestBody ChatRequest chatRequest) {
        return agent.preProcessChat(chatRequest);
    }
}
