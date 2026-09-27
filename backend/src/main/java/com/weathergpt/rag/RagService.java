package com.weathergpt.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class RagService {
    private static final List<String> KNOWLEDGE_FILES = List.of(
            "knowledge/disaster/lightning-safety.md",
            "knowledge/disaster/flood-safety.md",
            "knowledge/agriculture/rainfall-advisory.md",
            "knowledge/weather/precipitation-explanation.md"
    );

    private final ResourceLoader resourceLoader;
    private final EmbeddingStoreIngestor ingestor;
    private final ContentRetriever retriever;

    public RagService(ResourceLoader resourceLoader, EmbeddingStoreIngestor ingestor, ContentRetriever retriever) {
        this.resourceLoader = resourceLoader;
        this.ingestor = ingestor;
        this.retriever = retriever;
    }

    @PostConstruct
    void loadKnowledge() {
        for (String file : KNOWLEDGE_FILES) {
            try {
                Resource resource = resourceLoader.getResource("classpath:" + file);
                String text = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                ingestor.ingest(Document.from(text, Metadata.from("source", file)));
            } catch (Exception exception) {
                throw new IllegalStateException("Could not load knowledge document: " + file, exception);
            }
        }
    }

    public List<String> search(String question) {
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("Question is required");
        }
        return retriever.retrieve(Query.from(question)).stream()
                .map(Content::textSegment)
                .map(segment -> segment.text())
                .toList();
    }
}
