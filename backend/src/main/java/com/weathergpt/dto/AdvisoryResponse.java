package com.weathergpt.dto;

import java.util.List;

public record AdvisoryResponse(String location, String crop, String weatherForecast, String potentialImpact, List<String> recommendedActions, String warning, boolean demoData) {
}
