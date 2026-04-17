package org.prokopchuk.facultymcpserver.benchmark;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.prokopchuk.facultymcpserver.benchmark.dto.*;
import org.prokopchuk.facultymcpserver.benchmark.metrics.MetricsCalculator;
import org.prokopchuk.facultymcpserver.common.dto.SemanticSearchRequest;
import org.prokopchuk.facultymcpserver.common.dto.SemanticSearchResults;
import org.prokopchuk.facultymcpserver.service.FacultyDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.io.Resource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Log4j2
@Tag("semantic-search-benchmark")
@Testcontainers
@SpringBootTest
@TestPropertySource(properties = {
        "semantic-search-benchmark.test-docs.root=classpath:/semantic-search-benchmark"
})
public class SemanticSearchBenchmarkTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> POSTGRESQL_CONTAINER
            = new PostgreSQLContainer<>("pgvector/pgvector:pg16");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) throws IOException {
        Path tempStorage = Files.createTempDirectory("semantic-search-benchmark");
        registry.add("storage.documents.path", tempStorage::toString);
    }

    @Value("${semantic-search-benchmark.test-docs.root}")
    private Resource testDocsRoot;
    private Path testDocsRootDir;
    private Path testDocsDir;

    @Value("${storage.documents.path}")
    private String storageDocumentsPathStr;
    private Path storageDocumentsPathDir;

    @Autowired
    private FacultyDocumentService facultyDocumentService;

    @PostConstruct
    void init() throws IOException {
        this.testDocsRootDir = Path.of(testDocsRoot.getURI());
        this.testDocsDir = testDocsRootDir.resolve("docs");
        this.storageDocumentsPathDir = Path.of(storageDocumentsPathStr);

        FileUtils.cleanDirectory(storageDocumentsPathDir.toFile());
    }

    @PreDestroy
    void clean() throws IOException {
        FileUtils.deleteDirectory(this.storageDocumentsPathDir.toFile());
    }

    @Test
    void launchSemanticSearchBenchmark() throws IOException {
        saveDocuments();
        evaluateMetrics();
    }

    private void saveDocuments() throws IOException {
        log.info("Starting saving documents...");

        long totalStart = System.nanoTime();

        try (Stream<Path> stream = Files.list(testDocsDir)) {
            stream.filter(Files::isRegularFile)
                    .forEach(this::saveDocument);
        }

        long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - totalStart);

        log.info("Finish saving documents. Elapsed {} ms", elapsed);
    }

    private void saveDocument(Path file) {
        try {
            byte[] bytes = Files.readAllBytes(file);
            String fileName = file.getFileName().toString();

            long start = System.nanoTime();
            Long docId = facultyDocumentService.saveDocument(bytes, fileName);
            long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);

            log.info("Saved: {} -> id={} ({} ms)", fileName, docId, elapsed);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to save: " + file.getFileName(), e);
        }
    }

    private void evaluateMetrics() {
        TestDataset testDataset = readDataset();

        List<Integer> topKValues = List.of(1, 3, 5, 7);
        final double similarityThreshold = 0.5;

        log.info("Starting evaluating metrics. Questions count: {}", testDataset.questions().size());

        List<SemanticSearchMetrics> totalMetrics = new ArrayList<>();

        for (int topK : topKValues) {
            log.info("Starting evaluating with params: topK: {}, similarityThreshold: {}", topK, similarityThreshold);

            SemanticSearchMetrics metrics = evaluateMetricsForSpecifiedParams(testDataset, topK, similarityThreshold);
            totalMetrics.add(metrics);

            log.info("Evaluated metrics: {}", metrics);
        }

        printBenchmarkSummary(totalMetrics);
    }

    private TestDataset readDataset() {
        File questionsFile = new File(testDocsRootDir.toString(), "questions.json");
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());

        try {
            return objectMapper.readValue(questionsFile, TestDataset.class);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read json file: " + questionsFile, e);
        }
    }

    private SemanticSearchMetrics evaluateMetricsForSpecifiedParams(TestDataset testDataset, int topK, double similarityThreshold) {
        List<TestQuestionSearchResults> questionSearchResults = new ArrayList<>();

        long start = System.nanoTime();

        for (TestQuestion question : testDataset.questions()) {
            SemanticSearchResults searchResults = searchForQuestion(question, topK, similarityThreshold);
            questionSearchResults.add(new TestQuestionSearchResults(question, searchResults));
        }

        long durationMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);

        TestDatasetSearchResults totalSearchResults = new TestDatasetSearchResults(
                topK,
                similarityThreshold,
                questionSearchResults,
                durationMillis
        );

        return MetricsCalculator.calculateMetrics(totalSearchResults);
    }

    private SemanticSearchResults searchForQuestion(TestQuestion question, int topK, double similarityThreshold) {
        SemanticSearchRequest request = new SemanticSearchRequest(
                topK,
                question.question(),
                similarityThreshold
        );

        return facultyDocumentService.findBySemanticSearch(request);
    }

    private void printBenchmarkSummary(List<SemanticSearchMetrics> totalMetrics) {
        String header = String.format("%-6s %-10s %-12s %-12s %-12s %-12s %-12s",
                "Top-K", "Threshold", "Precision", "Recall", "MRR", "nDCG", "Time (ms)");
        String separator = "-".repeat(header.length());

        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(separator);
        sb.append("\n").append(header);
        sb.append("\n").append(separator);

        for (SemanticSearchMetrics m : totalMetrics) {
            sb.append(String.format("%n%-6d %-10.2f %-12.4f %-12.4f %-12.4f %-12.4f %-12.0f",
                    m.topK(),
                    m.similarityThreshold(),
                    m.precision(),
                    m.recall(),
                    m.mrr(),
                    m.ndcg(),
                    m.durationMillis()));
        }

        sb.append("\n").append(separator);

        log.info(sb.toString());
    }

}
