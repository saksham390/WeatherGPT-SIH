package com.weathergpt.dto;

public record AdvisoryRequest(String location, String crop, String growthStage, String soilType, String language) {
}
