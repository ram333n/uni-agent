package org.prokopchuk.facultymcpserver.benchmark.dto;

import lombok.Builder;

@Builder
public record SemanticSearchMetrics(
        int topK,
        double similarityThreshold,
        double durationMillis,
        double precision,
        double recall,
        double mrr,
        double ndcg
) {
}
