package com.weathergpt.model;

import java.time.LocalDate;

public record ForecastData(
        LocalDate date,
        double minTemperature,
        double maxTemperature,
        double rainfall,
        int precipitationProbability,
        double windSpeed,
        String weatherCondition,
        boolean demoData
) {
}
