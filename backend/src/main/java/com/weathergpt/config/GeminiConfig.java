package com.weathergpt.config;

import com.weathergpt.ai.WeatherAssistant;
import com.weathergpt.tools.WeatherTools;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.ObjectProvider;
import dev.langchain4j.rag.RetrievalAugmentor;

@Configuration
public class GeminiConfig {

    @Bean
    @ConditionalOnProperty(name = "gemini.enabled", havingValue = "true")
    GoogleAiGeminiChatModel geminiChatModel(
            @Value("${gemini.api-key}") String apiKey,
            @Value("${gemini.model:gemini-2.5-flash}") String model
    ) {
        return GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName(model)
                .temperature(0.2)
                .maxRetries(2)
                .build();
    }

    @Bean
    @ConditionalOnProperty(name = "gemini.enabled", havingValue = "true")
        WeatherAssistant weatherAssistant(GoogleAiGeminiChatModel chatModel, WeatherTools weatherTools, ObjectProvider<RetrievalAugmentor> retrievalAugmentorProvider) {
                var builder = AiServices.builder(WeatherAssistant.class)
                .chatModel(chatModel)
                                .tools(weatherTools);
                RetrievalAugmentor retrievalAugmentor = retrievalAugmentorProvider.getIfAvailable();
                if (retrievalAugmentor != null) {
                        builder.retrievalAugmentor(retrievalAugmentor);
                }
                return builder
                .chatMemoryProvider(sessionId -> MessageWindowChatMemory.builder()
                        .id(sessionId)
                        .maxMessages(12)
                        .build())
                .build();
    }
}
