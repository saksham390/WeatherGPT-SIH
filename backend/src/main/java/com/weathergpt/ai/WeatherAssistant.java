package com.weathergpt.ai;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface WeatherAssistant {
    @SystemMessage("""
            You are WeatherGPT, a careful weather intelligence assistant.
            Use weather tools for current and future weather. Never invent weather facts.
            Clearly distinguish demo data from live data. Do not reveal chain-of-thought.
            Give concise, practical answers and follow the user's requested language.
            """)
    String chat(@MemoryId String sessionId, @UserMessage String message);
}
