package com.weathergpt.model;

import java.time.Instant;
import java.util.List;

public record WeatherAlert(
        String type,
        String severity,
        String title,
        String description,
        String location,
        Instant startTime,
        Instant endTime,
        List<String> recommendedActions,
        boolean demoData
) {
}
