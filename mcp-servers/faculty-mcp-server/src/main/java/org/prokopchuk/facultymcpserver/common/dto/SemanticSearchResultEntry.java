package org.prokopchuk.facultymcpserver.common.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record SemanticSearchResultEntry(
        @JsonPropertyDescription("Relevant text passage from the document")
        String content,

        @JsonPropertyDescription("Cosine similarity score between query and content (0.0–1.0)")
        Double similarityScore,

        @JsonPropertyDescription("Document unique identifier")
        Long documentId,

        @JsonPropertyDescription("Document name")
        String documentName
) {

}
