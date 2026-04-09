package org.prokopchuk.facultymcpserver.benchmark.dto;

import java.util.List;

public record TestDatasetSearchResults(
        int topK,
        double similarityThreshold,
        List<TestQuestionSearchResults> questionsSearchResults,
        double durationMillis
) {
}
