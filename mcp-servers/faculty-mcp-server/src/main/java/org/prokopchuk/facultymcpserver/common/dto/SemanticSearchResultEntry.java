package org.prokopchuk.facultymcpserver.common.dto;

public record SemanticSearchResultEntry(
        String content,
        Double similarityScore,
        Long documentId
) {

}
