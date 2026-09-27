package com.weathergpt.controller;

import com.weathergpt.rag.RagService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.List;

@RestController
@RequestMapping("/api/rag")
@ConditionalOnProperty(name = "rag.enabled", havingValue = "true")
public class RagController {
    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    @GetMapping("/search")
    public List<String> search(@RequestParam String question) {
        return ragService.search(question);
    }
}
