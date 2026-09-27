package com.weathergpt.config;

import com.weathergpt.rag.RagService;
import dev.langchain4j.model.googleai.GoogleAiEmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "rag.enabled", havingValue = "true")
public class RagConfig {

    @Bean
    GoogleAiEmbeddingModel embeddingModel(
            @Value("${gemini.api-key}") String apiKey,
            @Value("${rag.embedding-model:text-embedding-004}") String model
    ) {
        return GoogleAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .modelName(model)
                .outputDimensionality(768)
                .build();
    }

    @Bean
    PgVectorEmbeddingStore embeddingStore(
            @Value("${database.host:localhost}") String host,
            @Value("${database.port:5432}") int port,
            @Value("${database.name:weathergpt}") String database,
            @Value("${database.username:weathergpt}") String username,
            @Value("${database.password:weathergpt}") String password,
            @Value("${rag.table:weathergpt_knowledge}") String table
    ) {
        return PgVectorEmbeddingStore.builder()
                .host(host)
                .port(port)
                .database(database)
                .user(username)
                .password(password)
                .table(table)
                .dimension(768)
                .createTable(true)
                .build();
    }

    @Bean
    EmbeddingStoreIngestor embeddingStoreIngestor(GoogleAiEmbeddingModel embeddingModel, PgVectorEmbeddingStore embeddingStore) {
        return EmbeddingStoreIngestor.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .documentSplitter(dev.langchain4j.data.document.splitter.DocumentSplitters.recursive(300, 30))
                .build();
    }

    @Bean
    ContentRetriever contentRetriever(GoogleAiEmbeddingModel embeddingModel, PgVectorEmbeddingStore embeddingStore) {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .maxResults(4)
                .minScore(0.55)
                .build();
    }

    @Bean
    RetrievalAugmentor retrievalAugmentor(ContentRetriever contentRetriever) {
        return DefaultRetrievalAugmentor.builder()
                .contentRetriever(contentRetriever)
                .build();
    }

    @Bean
    RagService ragService(org.springframework.core.io.ResourceLoader resourceLoader, EmbeddingStoreIngestor ingestor, ContentRetriever retriever) {
        return new RagService(resourceLoader, ingestor, retriever);
    }
}
