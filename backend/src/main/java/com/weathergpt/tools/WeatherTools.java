package com.weathergpt.tools;

import com.weathergpt.model.ForecastData;
import com.weathergpt.model.WeatherAlert;
import com.weathergpt.model.WeatherData;
import com.weathergpt.weather.WeatherService;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WeatherTools {
    private final WeatherService weatherService;

    public WeatherTools(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @Tool("Get the current weather for a location. Use this for current conditions.")
    public WeatherData getCurrentWeather(String location) {
        return weatherService.getCurrentWeather(location);
    }

    @Tool("Get a weather forecast for a location for 1 to 7 days.")
    public List<ForecastData> getForecast(String location, int days) {
        return weatherService.getForecast(location, days);
    }

    @Tool("Get deterministic weather alerts for a location.")
    public List<WeatherAlert> getWeatherAlerts(String location) {
        return weatherService.getAlerts(location);
    }
}
