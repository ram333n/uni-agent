package org.prokopchuk.facultymcpserver.benchmark;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.prokopchuk.facultymcpserver.common.dto.SemanticSearchRequest;
import org.prokopchuk.facultymcpserver.common.dto.SemanticSearchResultEntry;
import org.prokopchuk.facultymcpserver.service.FacultyDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Tag("benchmark")
@SpringBootTest
@ActiveProfiles("benchmark")
class SemanticSearchBenchmarkTest {

    private static final int TOP_K_RETRIEVAL = 20;
    private static final int[] K_VALUES = {1, 5, 10};

    @Autowired
    private FacultyDocumentService facultyDocumentService;

    @Value("${benchmark.similarity-threshold:0.3}")
    private double similarityThreshold;

    @Test
    void runBenchmark() throws IOException {
        List<BenchmarkQuery> queries = BenchmarkDataset.load();
        List<QueryResult> allResults = new ArrayList<>();

        for (BenchmarkQuery bq : queries) {
            Set<Long> relevantIds = new HashSet<>(bq.relevantDocumentIds());

            long start = System.currentTimeMillis();
            var searchResults = facultyDocumentService.findBySemanticSearch(
                    new SemanticSearchRequest(TOP_K_RETRIEVAL, bq.query(), similarityThreshold)
            );
            long latencyMs = System.currentTimeMillis() - start;

            List<Long> retrievedDocIds = searchResults.results().stream()
                    .map(SemanticSearchResultEntry::documentId)
                    .collect(Collectors.toList());

            Map<Integer, KMetrics> metricsByK = new LinkedHashMap<>();
            for (int k : K_VALUES) {
                metricsByK.put(k, new KMetrics(
                        BenchmarkMetricsCalculator.recallAtK(retrievedDocIds, relevantIds, k),
                        BenchmarkMetricsCalculator.precisionAtK(retrievedDocIds, relevantIds, k),
                        BenchmarkMetricsCalculator.mrrAtK(retrievedDocIds, relevantIds, k),
                        BenchmarkMetricsCalculator.ndcgAtK(retrievedDocIds, relevantIds, k)
                ));
            }

            allResults.add(new QueryResult(bq.queryId(), bq.query(), latencyMs, metricsByK, retrievedDocIds));
        }

        printTable(allResults);
        writeJsonReport(allResults);
    }

    private void printTable(List<QueryResult> results) {
        System.out.println("\n========== SEMANTIC SEARCH BENCHMARK RESULTS ==========");
        System.out.printf("%-6s | %-40s | %8s", "QueryId", "Query", "Latency");
        for (int k : K_VALUES) {
            System.out.printf(" | R@%-2d  P@%-2d  MRR@%-2d NDCG@%-2d", k, k, k, k);
        }
        System.out.println();
        System.out.println("-".repeat(120));

        for (QueryResult r : results) {
            System.out.printf("%-6s | %-40s | %6dms",
                    r.queryId(), truncate(r.query(), 40), r.latencyMs());
            for (int k : K_VALUES) {
                KMetrics m = r.metricsByK().get(k);
                System.out.printf(" | %.3f %.3f  %.3f  %.3f", m.recall(), m.precision(), m.mrr(), m.ndcg());
            }
            System.out.println();
        }

        System.out.println("-".repeat(120));
        System.out.println("AVERAGES:");
        System.out.printf("%-6s | %-40s | %6dms",
                "AVG", "", (long) results.stream().mapToLong(QueryResult::latencyMs).average().orElse(0));
        for (int k : K_VALUES) {
            int finalK = k;
            double avgRecall = results.stream().mapToDouble(r -> r.metricsByK().get(finalK).recall()).average().orElse(0);
            double avgPrec   = results.stream().mapToDouble(r -> r.metricsByK().get(finalK).precision()).average().orElse(0);
            double avgMrr    = results.stream().mapToDouble(r -> r.metricsByK().get(finalK).mrr()).average().orElse(0);
            double avgNdcg   = results.stream().mapToDouble(r -> r.metricsByK().get(finalK).ndcg()).average().orElse(0);
            System.out.printf(" | %.3f %.3f  %.3f  %.3f", avgRecall, avgPrec, avgMrr, avgNdcg);
        }
        System.out.println();
        System.out.println("=".repeat(120));
    }

    private void writeJsonReport(List<QueryResult> results) throws IOException {
        File targetDir = new File("target");
        targetDir.mkdirs();

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        File reportFile = new File(targetDir, "benchmark-results-" + timestamp + ".json");

        // Also write to a fixed name for easy CI consumption
        File latestFile = new File(targetDir, "benchmark-results.json");

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("timestamp", timestamp);
        report.put("similarityThreshold", similarityThreshold);
        report.put("topKRetrieval", TOP_K_RETRIEVAL);
        report.put("kValues", K_VALUES);
        report.put("queryResults", results);

        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(reportFile, report);
        mapper.writeValue(latestFile, report);

        System.out.println("Report written to: " + reportFile.getAbsolutePath());
    }

    private static String truncate(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }

    record KMetrics(double recall, double precision, double mrr, double ndcg) {}

    record QueryResult(
            String queryId,
            String query,
            long latencyMs,
            Map<Integer, KMetrics> metricsByK,
            List<Long> retrievedDocIds
    ) {}
}
