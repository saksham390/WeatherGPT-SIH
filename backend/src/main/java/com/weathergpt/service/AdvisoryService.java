package com.weathergpt.service;

import com.weathergpt.dto.AdvisoryRequest;
import com.weathergpt.dto.AdvisoryResponse;
import com.weathergpt.model.ForecastData;
import com.weathergpt.weather.WeatherService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdvisoryService {
    private final WeatherService weatherService;

    public AdvisoryService(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    public AdvisoryResponse create(AdvisoryRequest request) {
        if (request == null || request.location() == null || request.location().isBlank() || request.crop() == null || request.crop().isBlank()) {
            throw new IllegalArgumentException("Location and crop are required");
        }
        ForecastData forecast = weatherService.getForecast(request.location(), 1).getFirst();
        boolean hindi = "hi".equalsIgnoreCase(request.language());
        String weather = hindi ? "कल की वर्षा संभावना: " + forecast.precipitationProbability() + "%, अनुमानित वर्षा: " + forecast.rainfall() + " mm" : "Expected rainfall: " + forecast.rainfall() + " mm; precipitation probability: " + forecast.precipitationProbability() + "%";
        String impact = forecast.rainfall() >= 20 ? (hindi ? "भारी वर्षा से खेत में जलभराव या सिंचाई की आवश्यकता बदल सकती है।" : "Rain may reduce irrigation need and could create waterlogging risk.") : (hindi ? "हल्की वर्षा का प्रभाव सीमित हो सकता है।" : "The expected rain may have limited impact on the crop.");
        List<String> actions = hindi ? List.of("मिट्टी की नमी और जल निकासी जांचें।", "स्थानीय कृषि अधिकारी की सलाह लें।") : List.of("Check soil moisture and drainage before irrigating.", "Confirm crop-stage decisions with local agricultural guidance.");
        String warning = hindi ? "यह जानकारीपरक डेमो सलाह है, आधिकारिक कृषि निर्देशों का पालन करें।" : "This is informational demo advice; follow official agricultural guidance.";
        return new AdvisoryResponse(request.location().trim(), request.crop().trim(), weather, impact, actions, warning, forecast.demoData());
    }
}
