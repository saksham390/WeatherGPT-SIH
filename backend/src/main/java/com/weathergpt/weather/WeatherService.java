package com.weathergpt.weather;

import com.weathergpt.model.ForecastData;
import com.weathergpt.model.WeatherAlert;
import com.weathergpt.model.WeatherData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class WeatherService {
    private final WeatherProvider provider;
    private final double heavyRainThreshold;
    private final double highWindThreshold;
    private final double extremeHeatThreshold;

    public WeatherService(
            WeatherProvider provider,
            @Value("${weather.alerts.heavy-rain-mm:50}") double heavyRainThreshold,
            @Value("${weather.alerts.high-wind-kmh:60}") double highWindThreshold,
            @Value("${weather.alerts.extreme-heat-c:40}") double extremeHeatThreshold
    ) {
        this.provider = provider;
        this.heavyRainThreshold = heavyRainThreshold;
        this.highWindThreshold = highWindThreshold;
        this.extremeHeatThreshold = extremeHeatThreshold;
    }

    public WeatherData getCurrentWeather(String location) {
        return provider.getCurrentWeather(requireLocation(location));
    }

    public List<ForecastData> getForecast(String location, int days) {
        if (days < 1 || days > 7) {
            throw new IllegalArgumentException("Forecast days must be between 1 and 7");
        }
        return provider.getForecast(requireLocation(location), days);
    }

    public List<WeatherAlert> getAlerts(String location) {
        String validLocation = requireLocation(location);
        WeatherData current = provider.getCurrentWeather(validLocation);
        List<WeatherAlert> alerts = new ArrayList<>();
        if (current.rainfall() >= heavyRainThreshold) {
            alerts.add(alert("HEAVY_RAIN", "HIGH", "Heavy rain warning", "Heavy rainfall may cause waterlogging.", validLocation, List.of("Avoid low-lying roads.", "Monitor official advisories.")));
        }
        if (current.windSpeed() >= highWindThreshold) {
            alerts.add(alert("HIGH_WIND", "HIGH", "High wind warning", "Strong winds may make travel and outdoor work unsafe.", validLocation, List.of("Secure loose outdoor items.", "Avoid unnecessary travel.")));
        }
        if (current.temperature() >= extremeHeatThreshold) {
            alerts.add(alert("EXTREME_HEAT", "HIGH", "Extreme heat warning", "Very high temperatures increase heat stress risk.", validLocation, List.of("Stay hydrated.", "Avoid strenuous activity during the hottest hours.")));
        }
        if (current.weatherCondition().toLowerCase().contains("thunder")) {
            alerts.add(alert("THUNDERSTORM", "MODERATE", "Thunderstorm warning", "Thunderstorms may produce lightning and sudden wind.", validLocation, List.of("Move indoors and avoid exposed areas.")));
        }
        return alerts;
    }

    public List<WeatherData> getHistoricalWeather(String location, LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("A valid date range is required");
        }
        return provider.getHistoricalWeather(requireLocation(location), startDate, endDate);
    }

    private WeatherAlert alert(String type, String severity, String title, String description, String location, List<String> actions) {
        Instant now = Instant.now();
        return new WeatherAlert(type, severity, title, description, location, now, now.plusSeconds(86400), actions, true);
    }

    private String requireLocation(String location) {
        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException("Location is required");
        }
        return location.trim();
    }
}
