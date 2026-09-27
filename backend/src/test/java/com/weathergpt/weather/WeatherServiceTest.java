package com.weathergpt.weather;

import com.weathergpt.model.WeatherData;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WeatherServiceTest {

    private final WeatherProvider provider = new MockWeatherProvider();
    private final WeatherService service = new WeatherService(provider, 50, 60, 40);

    @Test
    void returnsClearlyLabeledDemoWeather() {
        WeatherData weather = service.getCurrentWeather("Delhi");

        assertThat(weather.location()).isEqualTo("Delhi");
        assertThat(weather.demoData()).isTrue();
        assertThat(weather.timestamp()).isBeforeOrEqualTo(Instant.now());
    }

    @Test
    void returnsRequestedForecastDays() {
        assertThat(service.getForecast("Delhi", 3)).hasSize(3);
    }

    @Test
    void rejectsInvalidForecastRange() {
        assertThatThrownBy(() -> service.getForecast("Delhi", 8))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("between 1 and 7");
    }

    @Test
    void returnsNoAlertsForTheDefaultDemoSnapshot() {
        assertThat(service.getAlerts("Delhi")).isEqualTo(List.of());
    }
}
