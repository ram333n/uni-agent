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
        int topK = searchResults.topK();

        return searchResults.questionsSearchResults().stream()
                .mapToDouble(questionResults -> evaluateNdcgForQuestion(questionResults, topK))
                .average()
                .orElseThrow();
    }

    private static double evaluateNdcgForQuestion(TestQuestionSearchResults questionResults, int topK) {
        Set<String> groundTruthDocs = new HashSet<>(questionResults.question().documents());

        List<String> rankedDocs = questionResults.searchResults().results().stream()
                .limit(topK)
                .map(SemanticSearchResultEntry::documentName)
                .toList();

        double dcg = computeDcg(rankedDocs, groundTruthDocs);
        double idcg = computeIdcg(groundTruthDocs, topK);

        return idcg == 0.0 ? 0.0 : dcg / idcg;
    }

    private static double computeDcg(List<String> rankedDocs, Set<String> groundTruthDocs) {
        double dcg = 0.0;
        Set<String> seen = new HashSet<>();

        for (int i = 0; i < rankedDocs.size(); i++) {
            String doc = rankedDocs.get(i);

            if (groundTruthDocs.contains(rankedDocs.get(i)) && seen.add(doc)) {
                dcg += 1.0 / (Math.log(i + 2) / Math.log(2));
            }
        }

        return dcg;
    }

    private static double computeIdcg(Set<String> groundTruthDocs, int topK) {
        int relevantInTopK = Math.min(groundTruthDocs.size(), topK);
        double idcg = 0.0;

        for (int i = 0; i < relevantInTopK; i++) {
            idcg += 1.0 / (Math.log(i + 2) / Math.log(2));
        }

        return idcg;
    }

}
