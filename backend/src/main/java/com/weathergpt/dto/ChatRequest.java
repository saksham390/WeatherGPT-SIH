package com.weathergpt.dto;

public record ChatRequest(String sessionId, String message, String language) {
}
