package org.prokopchuk.facultymcpserver.benchmark.dto;

import org.prokopchuk.facultymcpserver.common.dto.SemanticSearchResults;

public record TestQuestionSearchResults(
        TestQuestion question,
        SemanticSearchResults searchResults
) {

    public boolean hasSearchResults() {
        return searchResults.results() != null && !searchResults.results().isEmpty();
    }

}
