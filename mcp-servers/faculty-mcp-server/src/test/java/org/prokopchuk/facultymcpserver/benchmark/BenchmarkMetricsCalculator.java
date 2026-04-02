package org.prokopchuk.facultymcpserver.benchmark;

import java.util.List;
import java.util.Set;

/**
 * Pure stateless utility for computing IR retrieval metrics.
 * No Spring dependencies — fully unit-testable in isolation.
 */
public class BenchmarkMetricsCalculator {

    private BenchmarkMetricsCalculator() {}

    /**
     * Recall@K = |relevant ∩ retrieved[:K]| / |relevant|
     */
    public static double recallAtK(List<Long> retrievedDocIds, Set<Long> relevantDocIds, int k) {
        if (relevantDocIds.isEmpty()) return 0.0;
        long hits = retrievedDocIds.stream()
                .limit(k)
                .filter(relevantDocIds::contains)
                .distinct()
                .count();
        return (double) hits / relevantDocIds.size();
    }

    /**
     * Precision@K = |relevant ∩ retrieved[:K]| / K
     */
    public static double precisionAtK(List<Long> retrievedDocIds, Set<Long> relevantDocIds, int k) {
        if (k == 0) return 0.0;
        long hits = retrievedDocIds.stream()
                .limit(k)
                .filter(relevantDocIds::contains)
                .count();
        return (double) hits / k;
    }

    /**
     * MRR@K = 1 / rank_of_first_relevant (0 if none found in top-K)
     */
    public static double mrrAtK(List<Long> retrievedDocIds, Set<Long> relevantDocIds, int k) {
        for (int i = 0; i < Math.min(k, retrievedDocIds.size()); i++) {
            if (relevantDocIds.contains(retrievedDocIds.get(i))) {
                return 1.0 / (i + 1);
            }
        }
        return 0.0;
    }

    /**
     * NDCG@K = DCG@K / IDCG@K
     * Binary relevance: 1 if document is relevant, 0 otherwise.
     */
    public static double ndcgAtK(List<Long> retrievedDocIds, Set<Long> relevantDocIds, int k) {
        double dcg = dcg(retrievedDocIds, relevantDocIds, k);
        double idcg = idealDcg(relevantDocIds, k);
        if (idcg == 0.0) return 0.0;
        return dcg / idcg;
    }

    private static double dcg(List<Long> retrievedDocIds, Set<Long> relevantDocIds, int k) {
        double dcg = 0.0;
        for (int i = 0; i < Math.min(k, retrievedDocIds.size()); i++) {
            if (relevantDocIds.contains(retrievedDocIds.get(i))) {
                dcg += 1.0 / (Math.log(i + 2) / Math.log(2));
            }
        }
        return dcg;
    }

    private static double idealDcg(Set<Long> relevantDocIds, int k) {
        double idcg = 0.0;
        int numRelevant = Math.min(relevantDocIds.size(), k);
        for (int i = 0; i < numRelevant; i++) {
            idcg += 1.0 / (Math.log(i + 2) / Math.log(2));
        }
        return idcg;
    }
}
