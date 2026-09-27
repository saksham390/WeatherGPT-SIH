package com.weathergpt.weather;

import com.weathergpt.model.ForecastData;
import com.weathergpt.model.WeatherData;

import java.time.LocalDate;
import java.util.List;

public interface WeatherProvider {
    WeatherData getCurrentWeather(String location);

    List<ForecastData> getForecast(String location, int days);

    List<WeatherData> getHistoricalWeather(String location, LocalDate startDate, LocalDate endDate);
}
