package com.galicia.agentservice.client.llm;

public interface LlmProvider {
    /**
     * Genera texto desde un prompt.
     *
     * @param prompt El prompt.
     * @return Retorna texto plano desde el LLM.
     */
    String generateContent(String prompt);
}
