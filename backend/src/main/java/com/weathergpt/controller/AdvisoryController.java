package com.weathergpt.controller;

import com.weathergpt.dto.AdvisoryRequest;
import com.weathergpt.dto.AdvisoryResponse;
import com.weathergpt.service.AdvisoryService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/advisory")
public class AdvisoryController {
    private final AdvisoryService advisoryService;

    public AdvisoryController(AdvisoryService advisoryService) {
        this.advisoryService = advisoryService;
    }

    @PostMapping("/farmer")
    public AdvisoryResponse farmer(@RequestBody AdvisoryRequest request) {
        return advisoryService.create(request);
    }
}
