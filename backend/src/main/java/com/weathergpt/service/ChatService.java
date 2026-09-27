package com.weathergpt.service;

import com.weathergpt.ai.WeatherAssistant;
import com.weathergpt.dto.ChatRequest;
import com.weathergpt.dto.ChatResponse;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    private final ObjectProvider<WeatherAssistant> assistantProvider;

    public ChatService(ObjectProvider<WeatherAssistant> assistantProvider) {
        this.assistantProvider = assistantProvider;
    }

    public ChatResponse chat(ChatRequest request) {
        if (request == null || request.message() == null || request.message().isBlank()) {
            throw new IllegalArgumentException("Message is required");
        }
        String sessionId = request.sessionId() == null || request.sessionId().isBlank() ? "default" : request.sessionId();
        WeatherAssistant assistant = assistantProvider.getIfAvailable();
        if (assistant == null) {
            return new ChatResponse(sessionId, "Gemini is not enabled. Set GEMINI_API_KEY and GEMINI_ENABLED=true to use WeatherGPT chat.", false);
        }
        String languageInstruction = "hi".equalsIgnoreCase(request.language()) ? " Respond in Hindi." : " Respond in English.";
        return new ChatResponse(sessionId, assistant.chat(sessionId, request.message() + languageInstruction), true);
    }
}
