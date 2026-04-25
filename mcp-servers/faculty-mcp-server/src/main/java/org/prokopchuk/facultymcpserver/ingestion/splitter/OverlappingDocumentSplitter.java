package org.prokopchuk.facultymcpserver.ingestion.splitter;

import dev.langchain4j.data.document.DefaultDocument;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentTransformer;

import java.util.List;

public class OverlappingDocumentSplitter implements DocumentTransformer {

    private final DocumentSplitter internalSplitter;

    public OverlappingDocumentSplitter(
            int chunkSize,
            int chunkOverlappingSize
    ) {
        this.internalSplitter = DocumentSplitters.recursive(
                chunkSize,
                chunkOverlappingSize,
                new CL100KTokenEstimator()
        );
    }

    @Override
    public List<Document> apply(List<Document> documents) {
        if (CollectionUtils.isEmpty(documents)) {
            return List.of();
        }

        return documents.stream()
                .map(this::splitWithOverlap)
                .flatMap(List::stream)
                .toList();
    }

    private List<Document> splitWithOverlap(Document document) {
        List<TextSegment> chunks = internalSplitter.split(new DefaultDocument(document.getText()));

        return chunks.stream()
                .map(c -> new Document(c.text()))
                .toList();
    }

}
