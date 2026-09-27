package com.weathergpt.dto;

public record ChatResponse(String sessionId, String message, boolean aiEnabled) {
}
