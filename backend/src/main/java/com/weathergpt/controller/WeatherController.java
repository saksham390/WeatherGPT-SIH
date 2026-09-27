package com.weathergpt.controller;

import com.weathergpt.model.ForecastData;
import com.weathergpt.model.WeatherAlert;
import com.weathergpt.model.WeatherData;
import com.weathergpt.weather.WeatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {
    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/current")
    public WeatherData current(@RequestParam(defaultValue = "Delhi") String location) {
        return weatherService.getCurrentWeather(location);
    }

    @GetMapping("/forecast")
    public List<ForecastData> forecast(
            @RequestParam(defaultValue = "Delhi") String location,
            @RequestParam(defaultValue = "5") int days
    ) {
        return weatherService.getForecast(location, days);
    }

    @GetMapping("/alerts")
    public List<WeatherAlert> alerts(@RequestParam(defaultValue = "Delhi") String location) {
        return weatherService.getAlerts(location);
    }

    @GetMapping("/history")
    public List<WeatherData> history(
            @RequestParam(defaultValue = "Delhi") String location,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        LocalDate end = endDate == null ? LocalDate.now() : endDate;
        LocalDate start = startDate == null ? end.minusDays(6) : startDate;
        return weatherService.getHistoricalWeather(location, start, end);
    }
}
