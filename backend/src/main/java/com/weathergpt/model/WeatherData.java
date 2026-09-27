package com.weathergpt.model;

import java.time.Instant;

public record WeatherData(
        String location,
        double latitude,
        double longitude,
        double temperature,
        double feelsLike,
        int humidity,
        double windSpeed,
        String windDirection,
        double rainfall,
        int precipitationProbability,
        String weatherCondition,
        Instant timestamp,
        boolean demoData
) {
}
