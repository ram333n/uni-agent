package org.prokopchuk.facultymcpserver.benchmark.metrics;

import org.prokopchuk.facultymcpserver.benchmark.dto.SemanticSearchMetrics;
import org.prokopchuk.facultymcpserver.benchmark.dto.TestDatasetSearchResults;
import org.prokopchuk.facultymcpserver.benchmark.dto.TestQuestion;
import org.prokopchuk.facultymcpserver.benchmark.dto.TestQuestionSearchResults;
import org.prokopchuk.facultymcpserver.common.dto.SemanticSearchResultEntry;
import org.prokopchuk.facultymcpserver.common.dto.SemanticSearchResults;

import java.util.HashSet;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class MetricsCalculator {

    private MetricsCalculator() {}

    public static SemanticSearchMetrics calculateMetrics(TestDatasetSearchResults searchResults) {
        double precision = evaluatePrecision(searchResults);
        double recall = evaluateRecall(searchResults);
        double mrr = evaluateMeanReciprocalRank(searchResults);
        double ndcg = evaluateNormalizedDiscountedCumulativeGain(searchResults);

        return SemanticSearchMetrics.builder()
                .topK(searchResults.topK())
                .similarityThreshold(searchResults.similarityThreshold())
                .durationMillis(searchResults.durationMillis())
                .precision(precision)
                .recall(recall)
                .mrr(mrr)
                .ndcg(ndcg)
                .build();
    }

    private static double evaluatePrecision(TestDatasetSearchResults searchResults) {
        int topK = searchResults.topK();

        return searchResults.questionsSearchResults().stream()
                .mapToDouble(questionResults -> evaluatePrecisionForQuestion(questionResults, topK))
                .average()
                .orElseThrow();
    }

    private static double evaluatePrecisionForQuestion(TestQuestionSearchResults questionResults, int topK) {
        TestQuestion question = questionResults.question();
        SemanticSearchResults semanticSearchResults = questionResults.searchResults();

        Set<String> groundTruthDocs = new HashSet<>(question.documents());
        Set<String> predictedDocs = getPredictedDocs(semanticSearchResults, topK);

        long correctPredictionsCount = predictedDocs.stream()
                .filter(groundTruthDocs::contains)
                .count();

        int normalizer = Math.min(topK, groundTruthDocs.size());

        return normalizer == 0
                ? 0.0
                : (double) correctPredictionsCount / normalizer;
    }

    private static Set<String> getPredictedDocs(SemanticSearchResults results, int topK) {
        return results.results().stream()
                .limit(topK)
                .map(SemanticSearchResultEntry::documentName)
                .collect(Collectors.toSet());
    }

    private static double evaluateRecall(TestDatasetSearchResults searchResults) {
        int topK = searchResults.topK();

        return searchResults.questionsSearchResults().stream()
                .mapToDouble(questionResults -> evaluateRecallForQuestion(questionResults, topK))
                .average()
                .orElseThrow();
    }

    private static double evaluateRecallForQuestion(TestQuestionSearchResults questionResults, int topK) {
        TestQuestion question = questionResults.question();
        SemanticSearchResults semanticSearchResults = questionResults.searchResults();

        Set<String> groundTruthDocs = new HashSet<>(question.documents());
        Set<String> predictedDocs = getPredictedDocs(semanticSearchResults, topK);

        long correctPredictionsCount = predictedDocs.stream()
                .filter(groundTruthDocs::contains)
                .count();

        return (double) correctPredictionsCount / groundTruthDocs.size();
    }

    private static double evaluateMeanReciprocalRank(TestDatasetSearchResults searchResults) {
        return searchResults.questionsSearchResults().stream()
                .mapToDouble(questionResults -> {
                    List<String> groundTruthDocs = questionResults.question().documents();
                    SemanticSearchResults semanticSearchResults = questionResults.searchResults();

                    OptionalInt firstRelevantIdxOpt = IntStream.range(0, Math.min(semanticSearchResults.results().size(), searchResults.topK()))
                            .filter(i -> groundTruthDocs.contains(semanticSearchResults.results().get(i).documentName()))
                            .findFirst();

                    return firstRelevantIdxOpt.isPresent()
                            ? 1.0 / (firstRelevantIdxOpt.getAsInt() + 1)
                            : 0.0;
                })
                .average()
                .orElseThrow();
    }

    private static double evaluateNormalizedDiscountedCumulativeGain(TestDatasetSearchResults searchResults) {
        return 0.0; //TODO: implement
    }

}
