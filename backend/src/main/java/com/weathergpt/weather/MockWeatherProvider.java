package com.weathergpt.weather;

import com.weathergpt.model.ForecastData;
import com.weathergpt.model.WeatherData;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Component
@ConditionalOnProperty(name = "weather.provider", havingValue = "mock", matchIfMissing = true)
public class MockWeatherProvider implements WeatherProvider {

    @Override
    public WeatherData getCurrentWeather(String location) {
        String normalizedLocation = normalizeLocation(location);
        return new WeatherData(
                normalizedLocation,
                28.6139,
                77.2090,
                31.0,
                33.0,
                58,
                18.0,
                "NW",
                0.0,
                20,
                "Partly cloudy",
                Instant.now(),
                true
        );
    }

    @Override
    public List<ForecastData> getForecast(String location, int days) {
        normalizeLocation(location);
        return java.util.stream.IntStream.rangeClosed(1, days)
                .mapToObj(day -> new ForecastData(
                        LocalDate.now().plusDays(day),
                        24.0,
                        34.0,
                        day == 1 ? 18.0 : 4.0,
                        day == 1 ? 65 : 25,
                        day == 1 ? 24.0 : 14.0,
                        day == 1 ? "Light rain possible" : "Mostly sunny",
                        true
                ))
                .toList();
    }

    @Override
    public List<WeatherData> getHistoricalWeather(String location, LocalDate startDate, LocalDate endDate) {
        String normalizedLocation = normalizeLocation(location);
        return startDate.datesUntil(endDate.plusDays(1))
                .map(date -> new WeatherData(
                        normalizedLocation,
                        28.6139,
                        77.2090,
                        27.0,
                        28.0,
                        62,
                        12.0,
                        "W",
                        date.getDayOfMonth() % 3 == 0 ? 12.0 : 2.0,
                        date.getDayOfMonth() % 3 == 0 ? 55 : 15,
                        "Historical demo observation",
                        date.atStartOfDay(java.time.ZoneOffset.UTC).toInstant(),
                        true
                ))
                .toList();
    }

    private String normalizeLocation(String location) {
        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException("Location is required");
        }
        return location.trim();
    }
}
