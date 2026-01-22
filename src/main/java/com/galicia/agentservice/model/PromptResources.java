package com.galicia.agentservice.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PromptResources {
    WELCOME("welcome.st"),
    CONSTRUCT_FROM_PROMPT("construct-from-prompt.st"),
    COIN_DATA_LIST("coin-data-list.st");

    private final String value;

    @Override
    public String toString() {
        return value;
    }
}
