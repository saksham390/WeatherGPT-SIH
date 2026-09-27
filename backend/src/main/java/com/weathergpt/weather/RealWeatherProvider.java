package com.weathergpt.weather;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weathergpt.model.ForecastData;
import com.weathergpt.model.WeatherData;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@ConditionalOnProperty(name = "weather.provider", havingValue = "real")
public class RealWeatherProvider implements WeatherProvider {
    private final RestClient client = RestClient.create();
    private final ObjectMapper objectMapper;

    public RealWeatherProvider(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public WeatherData getCurrentWeather(String location) {
        JsonNode coordinates = geocode(location);
        JsonNode current = forecast(coordinates, 1).path("current");
        return new WeatherData(
                location.trim(), coordinates.path("latitude").asDouble(), coordinates.path("longitude").asDouble(),
                current.path("temperature_2m").asDouble(), current.path("apparent_temperature").asDouble(),
                current.path("relative_humidity_2m").asInt(), current.path("wind_speed_10m").asDouble(), "",
                current.path("precipitation").asDouble(), 0, weatherCode(current.path("weather_code").asInt()),
                Instant.now(), false
        );
    }

    @Override
    public List<ForecastData> getForecast(String location, int days) {
        JsonNode coordinates = geocode(location);
        JsonNode daily = forecast(coordinates, days).path("daily");
        List<ForecastData> result = new ArrayList<>();
        for (int index = 0; index < days; index++) {
            result.add(new ForecastData(
                    LocalDate.parse(daily.path("time").path(index).asText()),
                    daily.path("temperature_2m_min").path(index).asDouble(),
                    daily.path("temperature_2m_max").path(index).asDouble(),
                    daily.path("precipitation_sum").path(index).asDouble(),
                    daily.path("precipitation_probability_max").path(index).asInt(),
                    daily.path("wind_speed_10m_max").path(index).asDouble(),
                    weatherCode(daily.path("weather_code").path(index).asInt()), false
            ));
        }
        return result;
    }

    @Override
    public List<WeatherData> getHistoricalWeather(String location, LocalDate startDate, LocalDate endDate) {
        throw new UnsupportedOperationException("Historical live data is not configured for this provider");
    }

    private JsonNode geocode(String location) {
        try {
            String response = client.get().uri(uriBuilder -> uriBuilder
                    .scheme("https").host("geocoding-api.open-meteo.com").path("/v1/search")
                    .queryParam("name", location).queryParam("count", 1).queryParam("language", "en").build()).retrieve().body(String.class);
            JsonNode results = objectMapper.readTree(response).path("results");
            if (!results.isArray() || results.isEmpty()) throw new IllegalArgumentException("Location not found: " + location);
            return results.path(0);
        } catch (Exception exception) {
            throw new IllegalStateException("Could not retrieve live weather data right now", exception);
        }
    }

    private JsonNode forecast(JsonNode coordinates, int days) {
        try {
            String response = client.get().uri(uriBuilder -> uriBuilder
                    .scheme("https").host("api.open-meteo.com").path("/v1/forecast")
                    .queryParam("latitude", coordinates.path("latitude").asDouble())
                    .queryParam("longitude", coordinates.path("longitude").asDouble())
                    .queryParam("current", "temperature_2m,relative_humidity_2m,apparent_temperature,precipitation,wind_speed_10m,weather_code")
                    .queryParam("daily", "temperature_2m_max,temperature_2m_min,precipitation_sum,precipitation_probability_max,wind_speed_10m_max,weather_code")
                    .queryParam("forecast_days", days).queryParam("timezone", "auto").build()).retrieve().body(String.class);
            return objectMapper.readTree(response);
        } catch (Exception exception) {
            throw new IllegalStateException("Could not retrieve live weather data right now", exception);
        }
    }

    private String weatherCode(int code) {
        if (code == 0) return "Clear sky";
        if (code <= 3) return "Partly cloudy";
        if (code <= 67) return "Rain possible";
        if (code <= 77) return "Snow possible";
        if (code <= 99) return "Thunderstorm possible";
        return "Unknown conditions";
    }
}
