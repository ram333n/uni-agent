package org.prokopchuk.facultymcpserver.benchmark;

import java.util.List;

public record BenchmarkQuery(
        String queryId,
        String query,
        List<Long> relevantDocumentIds,
        List<String> relevantChunkKeywords
) {
}
