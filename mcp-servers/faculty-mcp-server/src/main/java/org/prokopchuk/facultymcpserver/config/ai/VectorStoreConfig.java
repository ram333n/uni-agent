package org.prokopchuk.facultymcpserver.config.ai;

import org.prokopchuk.facultymcpserver.ingestion.splitter.OverlappingDocumentSplitter;
import org.springframework.ai.document.DocumentTransformer;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgDistanceType.COSINE_DISTANCE;
import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgIndexType.NONE;

@Configuration
public class VectorStoreConfig {

    @Bean("facultyDocumentVectorStore")
    public VectorStore facultyDocumentVectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel) {
        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .vectorTableName("qwen_0_6b_document_embeddings")
                .initializeSchema(false)
                .dimensions(1024)
                .distanceType(COSINE_DISTANCE)
                .indexType(NONE)
                .schemaName("public")
                .maxDocumentBatchSize(1000)
                .build();
    }

//    @Bean
//    public TokenTextSplitter textSplitter() {
//        return TokenTextSplitter.builder() //TODO: no overlapping between chunks - need to implement!!!
//                .withChunkSize(800)
//                .withMinChunkSizeChars(300)
//                .withKeepSeparator(true)
//                .build();
//    }

    @Bean
    public DocumentTransformer textSplitter() {
        int chunkSize = 800;
        int overlapping = (int) (0.15 * chunkSize);

        return new OverlappingDocumentSplitter(
                chunkSize,
                overlapping
        );
    }

}
